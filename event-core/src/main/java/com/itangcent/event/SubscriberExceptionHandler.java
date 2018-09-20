package com.itangcent.event;

public interface SubscriberExceptionHandler {

    /**
     * Handles exceptions thrown by subscribers.
     */
    void handleException(Throwable exception, SubscriberExceptionContext context);
}
