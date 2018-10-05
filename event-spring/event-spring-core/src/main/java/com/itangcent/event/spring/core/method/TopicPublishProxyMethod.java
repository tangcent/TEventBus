package com.itangcent.event.spring.core.method;

import com.itangcent.event.EventBus;
import com.itangcent.event.TopicEvent;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@Deprecated
public class TopicPublishProxyMethod implements MethodInterceptor {

    private EventBus eventBus;
    private String[] topics;

    public TopicPublishProxyMethod(EventBus eventBus, String[] topics) {
        this.eventBus = eventBus;
        this.topics = topics;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public String[] getTopics() {
        return topics;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object returnVal = method.invoke(o, args);
        for (String topic : topics) {
            eventBus.post(new TopicEvent(returnVal, topic));
        }
        return returnVal;
    }


}
