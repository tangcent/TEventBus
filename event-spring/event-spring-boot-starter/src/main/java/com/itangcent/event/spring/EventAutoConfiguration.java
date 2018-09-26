package com.itangcent.event.spring;

import com.itangcent.event.DefaultSubscriberRegistry;
import com.itangcent.event.EventBus;
import com.itangcent.event.ExecutorDispatcher;
import com.itangcent.event.SubscriberRegistry;
import com.itangcent.event.local.LocalEventBus;
import com.itangcent.event.spring.core.*;
import com.itangcent.event.spring.utils.SpringBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.concurrent.Executors;

@EnableConfigurationProperties({EventAutoProperties.class})
public class EventAutoConfiguration {

    private final EventAutoProperties eventAutoProperties;

    public EventAutoConfiguration(EventAutoProperties eventAutoProperties) {
        this.eventAutoProperties = eventAutoProperties;
    }

    @Resource
    EventBusManager eventBusManager;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "tevent.autoRegistry", matchIfMissing = true)
    public SubscriberRegistry autoRegistry() {
        DefaultSubscriberRegistry autoSubscriberRegistry = new DefaultSubscriberRegistry();
        eventBusManager.setSubscriberRegistries(Collections.singletonList(autoSubscriberRegistry));
        return autoSubscriberRegistry;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "tevent.localEvent", matchIfMissing = true)
    public EventBus local() {
        int pool = eventAutoProperties.getLocalThread();
        if (pool == -1) {
            return new LocalEventBus(autoRegistry(), new ExecutorDispatcher());
        } else {
            return new LocalEventBus(autoRegistry(), new ExecutorDispatcher(Executors.newFixedThreadPool(pool)));
        }
    }

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