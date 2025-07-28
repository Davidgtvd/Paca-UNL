package org.unl.pacas.base.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/themes/imagenes/**")
                .addResourceLocations("file:src/main/frontend/themes/imagenes/");
        
        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations("file:src/main/frontend/themes/imagenes/");

        registry.addResourceHandler("/public/img/**")
                .addResourceLocations("file:src/main/frontend/public/img/");
        
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:src/main/frontend/public/img/");
    }
}