package com.itangcent.event.spring;

import com.itangcent.event.spring.core.EventBeanDefinitionRegistryPostProcessor;
import com.itangcent.event.spring.core.EventBeanPostProcessor;
import com.itangcent.event.spring.core.interceptor.DefaultEventInfoExtractor;
import com.itangcent.event.spring.core.interceptor.EventBeanFactoryAdvisor;
import com.itangcent.event.spring.core.interceptor.EventInfoExtractor;
import com.itangcent.event.spring.core.interceptor.EventInterceptor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

public class EventBeanAutoConfiguration {

    @Bean
    public EventBeanPostProcessor eventBeanPostProcessor() {
        return new EventBeanPostProcessor();
    }

    @Bean
    public EventBeanDefinitionRegistryPostProcessor eventBeanDefinitionRegistryPostProcessor() {
        return new EventBeanDefinitionRegistryPostProcessor();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public EventInfoExtractor eventInfoExtractor() {
        return new DefaultEventInfoExtractor();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public EventBeanFactoryAdvisor eventBeanFactoryAdvisor() {
        EventBeanFactoryAdvisor eventBeanFactoryAdvisor = new EventBeanFactoryAdvisor();
        eventBeanFactoryAdvisor.setEventInfoExtractor(eventInfoExtractor());
        eventBeanFactoryAdvisor.setAdvice(eventInterceptor());
        return eventBeanFactoryAdvisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public EventInterceptor eventInterceptor() {
        return EventInterceptor.instance();
    }
}
