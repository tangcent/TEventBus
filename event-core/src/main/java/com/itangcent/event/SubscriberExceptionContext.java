package com.itangcent.event;

public interface SubscriberExceptionContext extends SubscriberContext {
    int getRetriedTimes();

    void nextRetry();
}
