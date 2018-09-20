package com.itangcent.event;

public interface EventBusContext {
    public EventBus getEventBus();

    public int getMaxRetryTimes();
}
