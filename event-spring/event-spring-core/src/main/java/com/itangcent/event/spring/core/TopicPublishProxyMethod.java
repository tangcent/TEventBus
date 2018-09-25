package com.itangcent.event.spring.core;

import com.itangcent.event.EventBus;
import com.itangcent.event.TopicEvent;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class TopicPublishProxyMethod implements MethodInterceptor {

    private String[] topics;
    private EventBus[] eventBuses;

    public TopicPublishProxyMethod(String[] topics, EventBus[] eventBuses) {
        this.topics = topics;
        this.eventBuses = eventBuses;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object returnVal = method.invoke(o, args);
        for (EventBus eventBus : eventBuses) {
            for (String topic : topics) {
                eventBus.post(new TopicEvent(returnVal, topic));
            }
        }
        return returnVal;
    }


}
