package com.itangcent.event;

public interface SubscriberContext {
    public Object getEvent();

    public Subscriber getSubscriber();

    EventBus getEventBus();
}
