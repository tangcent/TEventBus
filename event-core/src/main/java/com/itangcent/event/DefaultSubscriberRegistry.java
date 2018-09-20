package com.itangcent.event;

import java.util.Iterator;

public class DefaultSubscriberRegistry implements SubscriberRegistry {


    @Override
    public void register(Object subscriber) {

    }

    @Override
    public void unregister(Object subscriber) {

    }

    @Override
    public Iterator<Subscriber> getSubscribers(Object event) {
        return null;
    }
}
