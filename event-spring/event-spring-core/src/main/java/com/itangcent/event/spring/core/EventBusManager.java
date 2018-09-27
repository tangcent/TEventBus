package com.itangcent.event.spring.core;

import com.itangcent.event.EventBus;
import com.itangcent.event.SubscriberRegistry;

import java.util.Collection;
import java.util.List;

public interface EventBusManager {
    void setSubscriberRegistries(List<SubscriberRegistry> subscriberRegistries);

    void addEventBusListeners(Object eventBusListener);

    EventBus eventBuses();

    EventBus getEventBus(String name);

    void addEventBus(String name, EventBus eventBus);
}
