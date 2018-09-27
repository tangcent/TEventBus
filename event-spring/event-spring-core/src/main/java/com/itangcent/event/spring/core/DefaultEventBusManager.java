package com.itangcent.event.spring.core;

import com.itangcent.event.EventBus;
import com.itangcent.event.SubscriberRegistry;
import com.itangcent.event.spring.utils.SpringBeanFactory;
import com.itangcent.event.utils.Assert;
import com.itangcent.event.utils.Collections;

import javax.annotation.Resource;
import java.util.*;

public class DefaultEventBusManager implements EventBusManager {

    @Resource
    private CompletedApplicationListener completedApplicationListener;

    private Map<String, EventBus> eventBusMap = new HashMap<>();

    private List<Object> eventBusListeners = new ArrayList<>();

    private List<SubscriberRegistry> subscriberRegistries = new ArrayList<>();

    @Override
    public void setSubscriberRegistries(List<SubscriberRegistry> subscriberRegistries) {
        Assert.notNull(subscriberRegistries);
        this.subscriberRegistries = subscriberRegistries;
        for (SubscriberRegistry registry : subscriberRegistries) {
            for (Object eventBusListener : eventBusListeners) {
                registry.register(eventBusListener);
            }
        }
    }

    @Override
    public void addEventBusListeners(Object eventBusListener) {
        this.eventBusListeners.add(eventBusListener);
        for (SubscriberRegistry registry : subscriberRegistries) {
            registry.register(eventBusListener);
        }
    }

    @Override
    public EventBus eventBuses() {
        DelegateEventBus eventBus = new DelegateEventBus();
        completedApplicationListener.addRefreshTasks(new EntiretyEventSearch(eventBus));
        return eventBus;
    }

    @Override
    public EventBus getEventBus(String name) {
        EventBus eventBus = eventBusMap.get(name);
        if (eventBus == null) {
            eventBus = new DelegateEventBus();
            completedApplicationListener.addRefreshTasks(new EventSearch(name, (DelegateEventBus) eventBus));
        }
        return eventBus;
    }

    @Override
    public void addEventBus(String name, EventBus eventBus) {
        eventBusMap.put(name, eventBus);
    }

    private class EventSearch implements Runnable {

        private String name;
        private DelegateEventBus delegateEventBus;

        public EventSearch(String name, DelegateEventBus delegateEventBus) {
            this.name = name;
            this.delegateEventBus = delegateEventBus;
        }

        @Override
        public void run() {
            Object eventBus = SpringBeanFactory.getBean(name);
            Assert.notNull(eventBus, "EventBus named [%s] not present!", name);
            Assert.isInstanceOf(EventBus.class, eventBus, "bean named [%s] should be a EventBus!", name);
            delegateEventBus.setDelegate((EventBus) eventBus);
        }
    }

    private class EntiretyEventSearch implements Runnable {

        private DelegateEventBus delegateEventBus;

        public EntiretyEventSearch(DelegateEventBus delegateEventBus) {
            this.delegateEventBus = delegateEventBus;
        }

        @Override
        public void run() {
            Assert.isTrue(!eventBusMap.isEmpty(), "No EventBus be found!");
            Collection<EventBus> eventBuses = eventBusMap.values();
            if (eventBuses.size() == 1) {
                delegateEventBus.setDelegate(Collections.first(eventBuses));
            } else {
                EventBus[] eventBusArr = eventBuses.toArray(new EventBus[eventBuses.size()]);
                delegateEventBus.setDelegate(new ComponentEventBus(eventBusArr));
            }
        }
    }
}
