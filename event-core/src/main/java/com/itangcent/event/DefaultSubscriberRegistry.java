package com.itangcent.event;

public class DefaultSubscriberRegistry extends AbstractSubscriberRegistry {
    @Override
    protected Subscriber buildSubscriber(Object subscriber, SubscriberMethod subscriberMethod) {
        return new DelegateMethodSubscriber(subscriber, subscriberMethod.getMethod(), subscriberMethod.getEventType());
    }
}
