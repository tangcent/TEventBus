package com.itangcent.event;

import com.itangcent.event.subscriber.Subscriber;
import com.itangcent.event.utils.Collections;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

public class ExecutorDispatcher extends AbstractDispatcher {

    private ExecutorService executorService;

    public ExecutorDispatcher(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    protected void dispatchEvents(Object event, Collection<Subscriber> subscribers, SubscriberExceptionHandler subscriberExceptionHandler) {
        if (subscribers.size() == 1) {
            Subscriber subscriber = Collections.first(subscribers);
            executorService.submit(() -> dispatch(event, subscriber, subscriberExceptionHandler));
        } else {
            executorService.submit(() -> {
                for (Subscriber subscriber : subscribers) {
                    executorService.submit(() -> dispatch(event, subscriber, subscriberExceptionHandler));
                }
            });
        }
    }
}
