package com.itangcent.event;

import com.itangcent.event.subscriber.Subscriber;

public interface SubscriberContext {
    public Object getEvent();

    public Subscriber getSubscriber();

    EventBus getEventBus();
}
