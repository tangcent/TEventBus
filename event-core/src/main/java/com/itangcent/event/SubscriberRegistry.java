package com.itangcent.event;

import java.util.Collection;

/**
 * provides ways for listeners to register subscriber to the EventBus.
 */
public interface SubscriberRegistry {
    public void register(Object subscriber);

    public void unregister(Object subscriber);

    Collection<Subscriber> getSubscribers(Object event);
}
