package com.itangcent.event.subscriber;

import com.itangcent.event.exceptions.EventException;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.Semaphore;

public class ConcurrencySubscriber implements DelegateSubscriber {
    private Subscriber delegateSubscriber;

    private Semaphore semaphore;

    public ConcurrencySubscriber(Subscriber delegateSubscriber, int concurrency) {
        this.delegateSubscriber = delegateSubscriber;
        this.semaphore = new Semaphore(concurrency);
    }

    @Override
    public void onSubscribe(Object event) {
        try {
            semaphore.acquire();
            try {
                delegateSubscriber.onSubscribe(event);
            } finally {
                semaphore.release();
            }
        } catch (InterruptedException e) {
            throw new EventException(e);
        }
    }

    @Override
    public Method getSubscriberMethod() {
        return delegateSubscriber.getSubscriberMethod();
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

    @Override
    public String toString() {
        return delegateSubscriber.toString();
    }

    @Override
    public Subscriber getDelegate() {
        return delegateSubscriber;
    }
}