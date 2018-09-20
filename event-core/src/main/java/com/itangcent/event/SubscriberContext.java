package com.itangcent.event;

public interface SubscriberContext extends EventBus {
    public Object getEvent();

    public Subscriber getSubscriber();
}
