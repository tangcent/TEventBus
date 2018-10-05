package com.itangcent.event.spring.core;

import com.itangcent.event.EventBus;
import com.itangcent.event.SubscriberRegistry;
import com.itangcent.event.utils.Assert;
import com.itangcent.event.utils.Collections;
import com.itangcent.event.utils.Runs;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.SmartInitializingSingleton;

import javax.annotation.Resource;
import java.util.*;

public class DefaultEventBusManager implements BeanFactoryAware, EventBusManager, SmartInitializingSingleton {

    private boolean initialized = false;

    @Resource
    private CompletedApplicationListener completedApplicationListener;

    private BeanFactory beanFactory;

    private Map<String, EventBus> eventBusMap = new HashMap<>();

    private List<Object> eventBusListeners = new ArrayList<>();

    private List<SubscriberRegistry> subscriberRegistries = new ArrayList<>();

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

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

    private Map<String, EventBus> busCache = new HashMap<>();

    @Override
    public EventBus eventBuses() {
        EventBus eventBus = busCache.get("");
        if (eventBus == null) {
            if (initialized) {
                eventBus = entiretyEventBus();
            } else {
                eventBus = new DelegateEventBus();
                completedApplicationListener.addRefreshTasks(new EntiretyEventSearch((DelegateEventBus) eventBus));
            }
            busCache.put("", eventBus);
        }
        return eventBus;
    }

    @Override
    public EventBus getEventBus(String name) {
        EventBus eventBus = eventBusMap.get(name);
        if (eventBus == null) {
            if (initialized) {
                throw new NoSuchBeanDefinitionException("no eventBus named [" + name + "]");
            }
            eventBus = busCache.get(name);
            if (eventBus == null) {
                eventBus = new DelegateEventBus();
                busCache.put(name, eventBus);
                completedApplicationListener.addRefreshTasks(new EventSearch(name, (DelegateEventBus) eventBus));
            }
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
            Object eventBus = Runs.safeCall(() -> beanFactory.getBean(name), null);
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
            delegateEventBus.setDelegate(entiretyEventBus());
        }
    }

    private EventBus entiretyEventBus() {
        Assert.isTrue(!eventBusMap.isEmpty(), "No EventBus be found!");
        Collection<EventBus> eventBuses = eventBusMap.values();
        if (eventBuses.size() == 1) {
            return Collections.first(eventBuses);
        } else {
            EventBus[] eventBusArr = eventBuses.toArray(new EventBus[eventBuses.size()]);
            return new ComponentEventBus(eventBusArr);
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.initialized = true;
    }
}
