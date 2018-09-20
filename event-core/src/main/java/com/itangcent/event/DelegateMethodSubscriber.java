package com.itangcent.event;

import com.itangcent.event.exceptions.EventException;
import com.itangcent.event.exceptions.EventSubscribeException;
import com.itangcent.event.utils.ReflectionUtils;

import java.lang.reflect.Method;
import java.text.MessageFormat;

public class DelegateMethodSubscriber implements Subscriber {

    private Object delegate;

    private Method method;

    private Class eventType;

    public DelegateMethodSubscriber(Object delegate, Method method, Class eventType) {
        this.delegate = delegate;
        this.method = method;
        this.eventType = eventType;
    }

    @Override
    public void onSubscribe(Object event) {
        try {
            method.invoke(delegate, event);
        } catch (EventException e) {
            throw e;
        } catch (Throwable ex) {
            throw new EventSubscribeException(MessageFormat.format("Failed to invoke target method ''{0}'' with arguments {1}",
                    ReflectionUtils.buildKey(delegate, method), event), ex);
        }
    }

    @Override
    public Method getSubscriberMethod() {
        return method;
    }

}
