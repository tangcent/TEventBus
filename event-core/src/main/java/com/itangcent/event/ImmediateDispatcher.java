package com.itangcent.event;

import java.util.Collection;

public class ImmediateDispatcher extends AbstractDispatcher {
    private static final ImmediateDispatcher INSTANCE = new ImmediateDispatcher();

    private ImmediateDispatcher() {
    }

    public static ImmediateDispatcher instance() {
        return INSTANCE;
    }

    @Override
    public void dispatch(Object event, Collection<Subscriber> subscribers, SubscriberExceptionHandler subscriberExceptionHandler) {
        for (Subscriber subscriber : subscribers) {
            dispatch(event, subscriber, subscriberExceptionHandler);
        }
    }
}
