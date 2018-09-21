package com.itangcent.event;

import com.itangcent.event.utils.ExceptionUtils;
import com.itangcent.event.utils.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Objects;

public class DelegateMethodSubscriber implements Subscriber {

    private Object delegate;

    private Method method;

    private Class eventType;

    public DelegateMethodSubscriber(Object delegate, Method method, Class eventType) {
        this.delegate = delegate;
        this.method = method;
        this.eventType = eventType;
        ReflectionUtils.makeAccessible(method);
    }

    @Override
    public void onSubscribe(Object event) {
        try {
            method.invoke(delegate, event);
        } catch (InvocationTargetException e) {
            ExceptionUtils.wrapAndThrow(e.getCause());
        } catch (Throwable ex) {
            ExceptionUtils.wrapAndThrow(ex);
        }
    }

    @Override
    public Method getSubscriberMethod() {
        return method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DelegateMethodSubscriber that = (DelegateMethodSubscriber) o;
        return Objects.equals(delegate, that.delegate) &&
                Objects.equals(method, that.method) &&
                Objects.equals(eventType, that.eventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate, method, eventType);
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0}'{'delegate={1}, method={2}, eventType={3}'}'",
                getClass().getSimpleName(), delegate, ReflectionUtils.buildMethod(method), eventType);
    }
}
