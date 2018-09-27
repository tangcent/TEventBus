package com.itangcent.event.spring.core;

import com.itangcent.event.EventBus;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DelegateEventBus that = (DelegateEventBus) o;
        return Objects.equals(delegate, that.delegate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DelegateEventBus{");
        sb.append("delegate=").append(delegate);
        sb.append('}');
        return sb.toString();
    }
}
