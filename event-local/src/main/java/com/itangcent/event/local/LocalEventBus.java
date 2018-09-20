package com.itangcent.event.local;

import com.itangcent.event.EventBus;
import com.itangcent.event.SubscriberRegistry;

public class LocalEventBus implements EventBus {
    private SubscriberRegistry subscriberRegistry;

    public void register(Object subscriber) {
        subscriberRegistry.register(subscriber);
    }

    public void unregister(Object subscriber) {
        subscriberRegistry.unregister(subscriber);
    }


    @Override
    public void post(Object event) {
        subscriberRegistry.getSubscribers(event);

    }
}
