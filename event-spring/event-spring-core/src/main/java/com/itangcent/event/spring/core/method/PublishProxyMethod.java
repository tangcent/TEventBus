package com.itangcent.event.spring.core.method;

import com.itangcent.event.EventBus;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@Deprecated
public class PublishProxyMethod implements MethodInterceptor {

    private EventBus eventBus;

    public PublishProxyMethod(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object returnVal = method.invoke(o, args);
        eventBus.post(returnVal);
        return returnVal;
    }
}
