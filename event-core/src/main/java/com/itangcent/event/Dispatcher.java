package com.itangcent.event;

import com.itangcent.event.subscriber.Subscriber;

public interface Dispatcher {
    void dispatch(Object event, Subscriber subscriber, SubscriberExceptionHandler subscriberExceptionHandler);
}
