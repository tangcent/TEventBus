package com.itangcent.event;

import com.itangcent.event.exceptions.EventException;

public abstract class AbstractDispatcher implements Dispatcher {
    protected void dispatch(Object event, Subscriber subscriber, SubscriberExceptionHandler subscriberExceptionHandler) {
        try {
            subscriber.onSubscribe(event);
        } catch (EventException e) {
            subscriberExceptionHandler.handleException(e.getCause(), new DefaultExceptionSubscriberContext(event, subscriber, null));
        } catch (Throwable e) {
            subscriberExceptionHandler.handleException(e, new DefaultExceptionSubscriberContext(event, subscriber, null));
        }
    }
}
