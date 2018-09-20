package com.itangcent.event;

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
