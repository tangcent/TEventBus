package com.itangcent.event.subscriber;

import java.lang.reflect.Method;

public interface Subscriber {
    void onSubscribe(final Object event);

    /**
     * @return The subscribed method of the Subscriber.
     */
    Method getSubscriberMethod();
}
