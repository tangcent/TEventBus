package com.itangcent.event.spring.core;

import com.itangcent.event.EventBus;
import com.itangcent.event.spring.utils.SpringBeanFactory;
import com.itangcent.event.utils.Assert;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DefaultEventBusManager implements EventBusManager {

    @Resource
    private CompletedApplicationListener completedApplicationListener;

    private Map<String, EventBus> eventBusMap = new HashMap<>();

    @Override
    public Collection<EventBus> eventBuses() {
        return eventBusMap.values();
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
            Assert.isInstanceOf(EventBus.class, eventBus, "bean named [%s] should be a EventBus!", name);
            delegateEventBus.setDelegate((EventBus) eventBus);
        }
    }
}
