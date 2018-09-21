package com.itangcent.event;

import com.itangcent.event.subscriber.Subscriber;

import java.util.concurrent.ExecutorService;

public class ExecutorDispatcher extends AbstractDispatcher {

    private ExecutorService executorService;

    public ExecutorDispatcher(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void dispatch(Object event, Subscriber subscriber, SubscriberExceptionHandler subscriberExceptionHandler) {
        executorService.submit(() -> dispatchEvents(event, subscriber, subscriberExceptionHandler));
    }

}
