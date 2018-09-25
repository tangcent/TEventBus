package com.itangcent.event.spring.core;

import com.itangcent.event.EventBus;

public class DelegateEventBus implements EventBus {

    private EventBus delegate;

    public void setDelegate(EventBus delegate) {
        this.delegate = delegate;
    }

    @Override
    public String name() {
        return delegate.name();
    }

    @Override
    public void post(Object event) {
        delegate.post(event);
    }
}
