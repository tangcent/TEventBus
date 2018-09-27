package com.itangcent.event.spring.core;

import com.itangcent.event.EventBus;

import java.util.Arrays;

public class ComponentEventBus implements EventBus {
    private EventBus[] eventBuses;

    public ComponentEventBus(EventBus[] eventBuses) {
        this.eventBuses = eventBuses;
    }

    @Override
    public String name() {
        return "ComponentEventBus[" + Arrays.stream(eventBuses)
                .map(EventBus::name)
                .reduce((a, b) -> a + "," + b)
                .orElse("") + "]";
    }

    @Override
    public void post(Object event) {
        for (EventBus eventBus : eventBuses) {
            eventBus.post(event);
        }
    }
}
