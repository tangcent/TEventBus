package com.itangcent.event;

import com.itangcent.event.subscriber.Subscriber;

import java.util.function.Consumer;

/**
 * provides ways for listeners to register subscriber to the EventBus.
 */
public interface SubscriberRegistry {
    public void register(Object subscriber);

    public void unregister(Object subscriber);

    void listen(SubscribeListener subscribeListener);

    void findSubscribers(EventBus eventBus, Object event, Consumer<Subscriber> subscriberConsumer);
}
