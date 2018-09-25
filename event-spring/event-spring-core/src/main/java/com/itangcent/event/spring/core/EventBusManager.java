package com.itangcent.event.spring.core;

import com.itangcent.event.EventBus;

import java.util.Collection;

public interface EventBusManager {
    Collection<EventBus> eventBuses();

    EventBus getEventBus(String name);

    void addEventBus(String name, EventBus eventBus);
}
