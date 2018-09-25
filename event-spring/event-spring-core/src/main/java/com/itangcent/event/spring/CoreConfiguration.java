package com.itangcent.event.spring;

import com.itangcent.event.spring.core.*;
import com.itangcent.event.spring.utils.SpringBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfiguration {

    @Bean
    public CompletedApplicationListener completedApplicationListener() {
        return new CompletedApplicationListener();
    }

    @Bean
    public SpringBeanFactory springBeanFactory() {
        return new SpringBeanFactory();
    }

    @Bean
    public EventBeanPostProcessor eventBeanPostProcessor() {
        return new EventBeanPostProcessor();
    }

    @Bean
    public EventBusManager eventBusManager() {
        return new DefaultEventBusManager();
    }

    @Bean
    public EventCglibProxyFactory eventCglibProxyFactory() {
        return new EventCglibProxyFactory();
    }
}
