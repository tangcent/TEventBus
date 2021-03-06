package com.itangcent.event.local;

import com.itangcent.event.*;

import java.util.concurrent.ExecutorService;

public class LocalEventBus extends AbstractEventBus {
    private SubscriberRegistry subscriberRegistry;
    private Dispatcher dispatcher;
    private SubscriberExceptionHandler subscriberExceptionHandler;

    public LocalEventBus() {
        subscriberRegistry = new DefaultSubscriberRegistry();
        dispatcher = ImmediateDispatcher.instance();
    }

    public LocalEventBus(int thread) {
        subscriberRegistry = new DefaultSubscriberRegistry();
        dispatcher = new ExecutorDispatcher(thread);
    }

    public LocalEventBus(SubscriberRegistry subscriberRegistry, Dispatcher dispatcher) {
        this.subscriberRegistry = subscriberRegistry;
        this.dispatcher = dispatcher;
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

    @Override
    public void post(Object event) {
        onSubscribe(event);
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

}
