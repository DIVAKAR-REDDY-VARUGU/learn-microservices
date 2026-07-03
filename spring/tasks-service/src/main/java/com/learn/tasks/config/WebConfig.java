package com.learn.tasks.config;

// Registers the LoggingInterceptor with Spring MVC (≈ binding an interceptor in a NestJS module).
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // a class that contributes configuration/beans to the app
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor()).addPathPatterns("/api/**"); // apply only to our API routes
    }
}
