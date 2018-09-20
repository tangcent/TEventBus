package com.itangcent.event.exceptions;

public class EventFailedException extends EventException {
    public EventFailedException(Throwable cause) {
        super("event failed,last error:" + cause.getMessage(), cause);
    }
}
