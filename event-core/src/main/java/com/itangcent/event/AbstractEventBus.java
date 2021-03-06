package com.itangcent.event;

import com.itangcent.event.annotation.Retry;
import com.itangcent.event.exceptions.EventFailedException;
import com.itangcent.event.utils.AnnotationUtils;
import com.itangcent.event.utils.ObjectUtils;
import com.itangcent.event.utils.RetryUtils;

public abstract class AbstractEventBus implements EventBus, Named {

    protected String name = getClass().getSimpleName();

    protected abstract SubscriberRegistry getSubscriberRegistry();

    protected abstract Dispatcher getDispatcher();

    protected abstract SubscriberExceptionHandler getSubscriberExceptionHandler();

    public void post(Object event, String topic) {
        post(new TopicEvent(event, topic));
    }

    protected void onSubscribe(Object event) {
        Dispatcher dispatcher = getDispatcher();
        getSubscriberRegistry().findSubscribers(this, event, subscriber -> {
            dispatcher.dispatch(event, subscriber, exceptionHandler);
        });
    }

    private BusSubscriberExceptionHandler exceptionHandler = new BusSubscriberExceptionHandler();

    private void reInvokeSubscriberAction(SubscriberExceptionContext context) throws Throwable {
        try {
            context.getSubscriber().onSubscribe(context.getEvent());
        } catch (Throwable e) {
            throw ObjectUtils.firstNonNull(e.getCause(), e);
        }
    }

    private class BusSubscriberExceptionHandler implements SubscriberExceptionHandler {

        @Override
        public void handleException(Throwable e, SubscriberExceptionContext context) {
            SubscriberExceptionContext wrapContext = new DefaultExceptionSubscriberContext(context.getEvent(), context.getSubscriber(), AbstractEventBus.this);

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
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
