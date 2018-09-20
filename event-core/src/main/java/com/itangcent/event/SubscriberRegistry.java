package com.itangcent.event;

/**
 * provides ways for listeners to register subscriber to the EventBus.
 */
public interface SubscriberRegistry {
    public void register(Object subscriber);

    public void unregister(Object subscriber);
}
