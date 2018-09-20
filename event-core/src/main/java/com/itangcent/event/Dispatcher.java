package com.itangcent.event;

import java.util.Collection;

public interface Dispatcher {
    void dispatch(Object event, Collection<Subscriber> subscribers, SubscriberExceptionHandler subscriberExceptionHandler);
}
