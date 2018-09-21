package com.itangcent.event;

import com.itangcent.event.subscriber.Subscriber;

import java.util.Collection;

public interface Dispatcher {
    void dispatch(Object event, Subscriber subscriber, SubscriberExceptionHandler subscriberExceptionHandler);
}
