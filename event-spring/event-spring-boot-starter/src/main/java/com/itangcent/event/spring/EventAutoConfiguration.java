package com.itangcent.event.spring;

import com.itangcent.event.DefaultSubscriberRegistry;
import com.itangcent.event.EventBus;
import com.itangcent.event.ExecutorDispatcher;
import com.itangcent.event.SubscriberRegistry;
import com.itangcent.event.local.LocalEventBus;
import com.itangcent.event.spring.core.DefaultEventBusManager;
import com.itangcent.event.spring.core.EventBusManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.concurrent.Executors;

@EnableConfigurationProperties({TEventProperties.class})
public class EventAutoConfiguration {

    private TEventProperties eventAutoProperties;

    public EventAutoConfiguration(TEventProperties eventAutoProperties) {
        this.eventAutoProperties = eventAutoProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "tevent.autoRegistry", matchIfMissing = true)
    public SubscriberRegistry autoRegistry(EventBusManager eventBusManager) {
        DefaultSubscriberRegistry autoSubscriberRegistry = new DefaultSubscriberRegistry();
        eventBusManager.setSubscriberRegistries(Collections.singletonList(autoSubscriberRegistry));
        return autoSubscriberRegistry;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "tevent.localEvent", matchIfMissing = true)
    public EventBus localEventBus(EventBusManager eventBusManager) {
        int pool = eventAutoProperties.getLocalThread();
        if (pool == -1) {
            return new LocalEventBus(autoRegistry(eventBusManager), new ExecutorDispatcher());
        } else {
            return new LocalEventBus(autoRegistry(eventBusManager), new ExecutorDispatcher(pool));
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public EventBusManager eventBusManager() {
        return new DefaultEventBusManager();
    }
}
