package org.unl.pacas.base.controller.data_struct.queque;

public class Queue <E> {
    private QuequeImplementation<E> queue;
    public Queue(Integer top) {
        queue = new QuequeImplementation<>(top);
    }
    public Boolean queue(E data) {
        try {
            queue.queue(data);
            return true;
        } catch (Exception e) {
            return false;
            // TODO: handle exception
        }
    }

    public E dequeue() {
        try {
            return queue.dequeue();
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }

    public Boolean isFullQueue() {
        return queue.isFullQueque();
    }

    public Integer top() {
        return queue.getTop();
    }

    public Integer size() {
        return queue.getLength();
    }
}
