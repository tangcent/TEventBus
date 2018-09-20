package com.itangcent.event;

import java.util.Iterator;

public abstract class AbstractEventBus implements EventBus {

    protected abstract SubscriberRegistry getSubscriberRegistry();

    protected abstract Dispatcher getDispatcher();

    public void post(Object event, String topic) {
        post(new TopicEvent(event, topic));
    }

    @Override
    public void post(Object event) {
        Iterator<Subscriber> subscribers = getSubscriberRegistry().getSubscribers(event);
        getDispatcher().dispatch(event, subscribers);
    }
}
