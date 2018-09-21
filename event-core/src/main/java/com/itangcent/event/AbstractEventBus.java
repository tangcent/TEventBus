package com.itangcent.event;

import com.itangcent.event.annotation.Retry;
import com.itangcent.event.exceptions.EventFailedException;
import com.itangcent.event.utils.AnnotationUtils;
import com.itangcent.event.utils.ObjectUtils;
import com.itangcent.event.utils.RetryUtils;

import java.util.Collection;

public abstract class AbstractEventBus implements EventBus, SubscriberExceptionHandler {

    protected abstract SubscriberRegistry getSubscriberRegistry();

    protected abstract Dispatcher getDispatcher();

    protected abstract SubscriberExceptionHandler getSubscriberExceptionHandler();

    public void post(Object event, String topic) {
        post(new TopicEvent(event, topic));
    }

    @Override
    public void post(Object event) {
        Collection<Subscriber> subscribers = getSubscriberRegistry().getSubscribers(this, event);
        getDispatcher().dispatch(event, subscribers, this);
    }

    @Override
    public void handleException(Throwable e, SubscriberExceptionContext context) {
        SubscriberExceptionContext wrapContext = new DefaultExceptionSubscriberContext(context.getEvent(), context.getSubscriber(), this);

        SubscriberExceptionHandler subscriberExceptionHandler = getSubscriberExceptionHandler();
        handleException(subscriberExceptionHandler, e, wrapContext);

        //get maxRetryTimes
        Retry retry = AnnotationUtils.getAnnotation(context.getSubscriber().getSubscriberMethod(), Retry.class);
        int retryTimes = RetryUtils.maxRetryTimes(retry, e, 1);
        //retry
        if (retryTimes > 0) {
            while (wrapContext.getRetriedTimes() < retryTimes) {
                try {
                    reInvokeSubscriberAction(wrapContext);
                    return;
                } catch (Throwable re) {
                    wrapContext.nextRetry();
                    e = re;
                    handleException(subscriberExceptionHandler, re, wrapContext);
                }
            }
            handleException(subscriberExceptionHandler, new EventFailedException(e), wrapContext);
        }
    }

    void handleException(SubscriberExceptionHandler subscriberExceptionHandler, Throwable exception, SubscriberExceptionContext context) {
        if (subscriberExceptionHandler != null) {
            subscriberExceptionHandler.handleException(exception, context);
        }
    }

    private void reInvokeSubscriberAction(SubscriberExceptionContext context) throws Throwable {
        try {
            context.getSubscriber().onSubscribe(context.getEvent());
        } catch (Throwable e) {
            throw ObjectUtils.firstNonNull(e.getCause(), e);
        }
    }
}
