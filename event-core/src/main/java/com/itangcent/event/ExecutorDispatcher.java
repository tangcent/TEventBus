package com.itangcent.event;

import com.itangcent.event.subscriber.Subscriber;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorDispatcher extends AbstractDispatcher {

    private ExecutorService executorService;

    public ExecutorDispatcher(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public ExecutorDispatcher() {
        this(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
    }

    @Override
    public void dispatch(Object event, Subscriber subscriber, SubscriberExceptionHandler subscriberExceptionHandler) {
        executorService.submit(() -> dispatchEvents(event, subscriber, subscriberExceptionHandler));
    }

}
