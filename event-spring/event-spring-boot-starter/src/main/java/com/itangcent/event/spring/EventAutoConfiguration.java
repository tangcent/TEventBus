package com.itangcent.event.spring;

import com.itangcent.event.spring.core.*;
import com.itangcent.event.spring.utils.SpringBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@EnableAutoConfiguration
public class EventAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CompletedApplicationListener completedApplicationListener() {
        return new CompletedApplicationListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringBeanFactory springBeanFactory() {
        return new SpringBeanFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public EventBeanPostProcessor eventBeanPostProcessor() {
        return new EventBeanPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public EventBusManager eventBusManager() {
        return new DefaultEventBusManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public EventCglibProxyFactory eventCglibProxyFactory() {
        return new EventCglibProxyFactory();
    }
}
