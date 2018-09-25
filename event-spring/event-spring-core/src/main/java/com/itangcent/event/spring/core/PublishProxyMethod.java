package com.itangcent.event.spring.core;

import com.itangcent.event.EventBus;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class PublishProxyMethod implements MethodInterceptor {

    private EventBus[] eventBuses;

    public PublishProxyMethod(EventBus[] eventBuses) {
        this.eventBuses = eventBuses;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object returnVal = method.invoke(o, args);
        for (EventBus eventBus : eventBuses) {
            eventBus.post(returnVal);
        }
        return returnVal;
    }
}
