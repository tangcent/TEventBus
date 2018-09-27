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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentEventBus that = (ComponentEventBus) o;
        return Arrays.equals(eventBuses, that.eventBuses);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(eventBuses);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ComponentEventBus{");
        sb.append("eventBuses=").append(Arrays.toString(eventBuses));
        sb.append('}');
        return sb.toString();
    }
}
