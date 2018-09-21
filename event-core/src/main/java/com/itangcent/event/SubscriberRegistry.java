package com.itangcent.event;

import com.itangcent.event.subscriber.Subscriber;

import java.util.Collection;

/**
 * provides ways for listeners to register subscriber to the EventBus.
 */
public interface SubscriberRegistry {
    public void register(Object subscriber);

    public void unregister(Object subscriber);

    Collection<Subscriber> getSubscribers(EventBus eventBus, Object event);
}
