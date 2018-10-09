package com.itangcent.event.subscriber;

import com.itangcent.event.Prioritized;

import java.lang.reflect.Method;

public class PrioritizedSubscriber implements DelegateSubscriber, Prioritized<PrioritizedSubscriber> {
    private Subscriber delegateSubscriber;
    private int priority;

    public PrioritizedSubscriber(Subscriber delegateSubscriber, int priority) {
        this.delegateSubscriber = delegateSubscriber;
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public Subscriber getDelegate() {
        return delegateSubscriber;
    }

    @Override
    public void onSubscribe(Object event) {
        delegateSubscriber.onSubscribe(event);
    }

    @Override
    public Method getSubscriberMethod() {
        return delegateSubscriber.getSubscriberMethod();
    }
}
