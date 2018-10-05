package com.itangcent.event.spring;

import com.itangcent.event.spring.core.CompletedApplicationListener;
import com.itangcent.event.spring.core.DefaultEventBusManager;
import com.itangcent.event.spring.core.EventBeanPostProcessor;
import com.itangcent.event.spring.core.EventBusManager;
import com.itangcent.event.spring.core.interceptor.DefaultEventInfoExtractor;
import com.itangcent.event.spring.core.interceptor.EventBeanFactoryAdvisor;
import com.itangcent.event.spring.core.interceptor.EventInfoExtractor;
import com.itangcent.event.spring.core.interceptor.EventInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventCoreConfiguration {

    @Bean
    public CompletedApplicationListener completedApplicationListener() {
        return new CompletedApplicationListener();
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
    public EventInfoExtractor eventInfoExtractor() {
        return new DefaultEventInfoExtractor();
    }

    @Bean
    public EventBeanFactoryAdvisor eventBeanFactoryAdvisor() {
        EventBeanFactoryAdvisor eventBeanFactoryAdvisor = new EventBeanFactoryAdvisor();
        eventBeanFactoryAdvisor.setEventInfoExtractor(eventInfoExtractor());
        return eventBeanFactoryAdvisor;
    }

    @Bean
    public EventInterceptor eventInterceptor() {
        return new EventInterceptor();
    }
//
//    @Bean
//    public EventCglibProxyFactory eventCglibProxyFactory() {
//        return new EventCglibProxyFactory();
//    }
}
