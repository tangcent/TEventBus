package com.itangcent.event.local;

import com.itangcent.event.AbstractEventBus;
import com.itangcent.event.DefaultSubscriberRegistry;
import com.itangcent.event.Dispatcher;
import com.itangcent.event.SubscriberRegistry;

public class LocalEventBus extends AbstractEventBus {
    private SubscriberRegistry subscriberRegistry;
    private Dispatcher dispatcher;

    public LocalEventBus() {
        subscriberRegistry = new DefaultSubscriberRegistry();
    }

    @Override
    protected SubscriberRegistry getSubscriberRegistry() {
        return subscriberRegistry;
    }

    @Override
    protected Dispatcher getDispatcher() {
        return dispatcher;
    }

    public void register(Object subscriber) {
        subscriberRegistry.register(subscriber);
    }

    public void unregister(Object subscriber) {
        subscriberRegistry.unregister(subscriber);
    }
}
