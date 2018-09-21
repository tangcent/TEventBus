package com.itangcent.event.subscriber;

import com.itangcent.event.EventBus;

import java.lang.reflect.Method;
import java.util.Objects;

public class FilteredSubscriber implements DelegateSubscriber, SubscriberFilter {
    private Subscriber delegateSubscriber;

    private SubscriberFilter subscriberFilter;

    public FilteredSubscriber(Subscriber delegateSubscriber, SubscriberFilter subscriberFilter) {
        this.delegateSubscriber = delegateSubscriber;
        this.subscriberFilter = subscriberFilter;
    }

    @Override
    public boolean canSubscribe(EventBus eventBus, String topic, Object event) {
        return subscriberFilter.canSubscribe(eventBus, topic, event);
    }

    @Override
    public void onSubscribe(Object event) {
        delegateSubscriber.onSubscribe(event);
    }

    @Override
    public Method getSubscriberMethod() {
        return delegateSubscriber.getSubscriberMethod();
    }

    @Override
    public Subscriber getDelegate() {
        return delegateSubscriber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof DelegateSubscriber)) return false;
        DelegateSubscriber that = (DelegateSubscriber) o;
        return Objects.equals(delegateSubscriber, that.getDelegate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegateSubscriber);
    }
}
