package com.itangcent.event.local;

import com.itangcent.event.*;

public class LocalEventBus extends AbstractEventBus {
    private SubscriberRegistry subscriberRegistry;
    private Dispatcher dispatcher;
    private SubscriberExceptionHandler subscriberExceptionHandler;
    private String name = "LocalEventBus";

    public LocalEventBus() {
        subscriberRegistry = new DefaultSubscriberRegistry();
        dispatcher = ImmediateDispatcher.instance();
    }

    @Override
    protected SubscriberRegistry getSubscriberRegistry() {
        return subscriberRegistry;
    }

    @Override
    protected Dispatcher getDispatcher() {
        return dispatcher;
    }

    @Override
    protected SubscriberExceptionHandler getSubscriberExceptionHandler() {
        return subscriberExceptionHandler;
    }

    public void setSubscriberExceptionHandler(SubscriberExceptionHandler subscriberExceptionHandler) {
        this.subscriberExceptionHandler = subscriberExceptionHandler;
    }

    public void register(Object subscriber) {
        subscriberRegistry.register(subscriber);
    }

    public void unregister(Object subscriber) {
        subscriberRegistry.unregister(subscriber);
    }

    @Override
    public String name() {
        return name;
    }
}
