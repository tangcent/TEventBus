package com.itangcent.event;

import com.itangcent.event.subscriber.Subscriber;

public class DefaultExceptionSubscriberContext extends DefaultSubscriberContext implements SubscriberExceptionContext {
    private int times = 0;

    public DefaultExceptionSubscriberContext(Object event, Subscriber subscriber, EventBus eventBus) {
        super(event, subscriber, eventBus);
    }

    @Override
    public int getRetriedTimes() {
        return times;
    }

    @Override
    public void nextRetry() {
        ++times;
    }
}
