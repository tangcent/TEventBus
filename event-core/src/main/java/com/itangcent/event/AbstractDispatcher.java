package com.itangcent.event;

import com.itangcent.event.exceptions.EventException;
import com.itangcent.event.subscriber.Subscriber;

import java.util.Collection;

public abstract class AbstractDispatcher implements Dispatcher {
    @Override
    public void dispatch(Object event, Collection<Subscriber> subscribers, SubscriberExceptionHandler subscriberExceptionHandler) {
        dispatchEvents(event, subscribers, subscriberExceptionHandler);
    }

    protected abstract void dispatchEvents(Object event, Collection<Subscriber> subscribers, SubscriberExceptionHandler subscriberExceptionHandler);

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
