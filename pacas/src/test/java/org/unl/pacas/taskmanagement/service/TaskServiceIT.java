package org.unl.pacas.taskmanagement.service;

import org.unl.pacas.TestcontainersConfiguration;
import org.unl.pacas.security.dev.SampleUsers;
import org.unl.pacas.taskmanagement.domain.Task;
import org.unl.pacas.taskmanagement.domain.TaskRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class TaskServiceIT {

    @Autowired
    TaskService taskService;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    Clock clock;

    @AfterEach
    void cleanUp() {
        taskRepository.deleteAll();
    }

    @Test
    @WithUserDetails(SampleUsers.USER_USERNAME)
    public void tasks_are_stored_in_the_database_with_the_current_timestamp() {
        var now = clock.instant();
        var due = LocalDate.of(2025, 2, 7);
        taskService.createTask("Do this", due);
        assertThat(taskService.list(PageRequest.ofSize(1))).singleElement()
                .matches(task -> task.getDescription().equals("Do this") && due.equals(task.getDueDate())
                        && task.getCreationDate().isAfter(now));
    }

    @Test
    @WithUserDetails(SampleUsers.ADMIN_USERNAME)
    public void tasks_are_validated_before_they_are_stored() {
        assertThatThrownBy(() -> taskService.createTask("X".repeat(Task.DESCRIPTION_MAX_LENGTH + 1), null))
                .isInstanceOf(ValidationException.class);
        assertThat(taskRepository.count()).isEqualTo(0);
    }
}
