package com.itangcent.event;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

public class ExecutorDispatcher extends AbstractDispatcher {

    private ExecutorService executorService;

    public ExecutorDispatcher(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    protected void dispatchEvents(Object event, Collection<Subscriber> subscribers, SubscriberExceptionHandler subscriberExceptionHandler) {
        for (Subscriber subscriber : subscribers) {
            executorService.submit(() -> dispatch(event, subscriber, subscriberExceptionHandler));
        }
    }
}
