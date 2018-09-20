package com.itangcent.event.exceptions;

public class EventErrorException extends EventException {
    public EventErrorException(Throwable cause) {
        super("event error:" + cause.getMessage(), cause);
    }
}
