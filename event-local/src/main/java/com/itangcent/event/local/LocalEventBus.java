package com.itangcent.event.local;

import com.itangcent.event.AbstractEventBus;
import com.itangcent.event.DefaultSubscriberRegistry;
import com.itangcent.event.SubscriberRegistry;

public class LocalEventBus extends AbstractEventBus {
    private SubscriberRegistry subscriberRegistry;

    public LocalEventBus() {
        subscriberRegistry = new DefaultSubscriberRegistry();
    }

    @Override
    protected SubscriberRegistry getSubscriberRegistry() {
        return subscriberRegistry;
    }

    public void register(Object subscriber) {
        subscriberRegistry.register(subscriber);
    }

    public void unregister(Object subscriber) {
        subscriberRegistry.unregister(subscriber);
    }
}
