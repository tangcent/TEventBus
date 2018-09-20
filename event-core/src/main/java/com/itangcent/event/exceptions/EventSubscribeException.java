package com.itangcent.event.exceptions;

public class EventSubscribeException extends EventException {
    public EventSubscribeException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventSubscribeException(String message) {
        super(message);
    }
}
