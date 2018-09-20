package com.itangcent.event;

import com.itangcent.event.annotation.Subscribe;

import java.lang.reflect.Method;

public class SubscriberMethod {

    private Method method;

    private Class eventType;

    private Subscribe subscribe;

    public SubscriberMethod() {
    }

    public SubscriberMethod(Method method, Class eventType) {
        this.method = method;
        this.eventType = eventType;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class getEventType() {
        return eventType;
    }

    public void setEventType(Class eventType) {
        this.eventType = eventType;
    }

    public Subscribe getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Subscribe subscribe) {
        this.subscribe = subscribe;
    }
}
