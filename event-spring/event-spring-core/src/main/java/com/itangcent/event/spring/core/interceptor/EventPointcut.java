package com.itangcent.event.spring.core.interceptor;

import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;

public class EventPointcut extends StaticMethodMatcherPointcut {
    private EventInfoExtractor eventInfoExtractor;

    public EventPointcut() {
    }

    public EventPointcut(EventInfoExtractor eventInfoExtractor) {
        this.eventInfoExtractor = eventInfoExtractor;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return (eventInfoExtractor != null && !CollectionUtils.isEmpty(eventInfoExtractor.extract(method, targetClass)));
    }

    public EventInfoExtractor getEventInfoExtractor() {
        return eventInfoExtractor;
    }

    public void setEventInfoExtractor(EventInfoExtractor eventInfoExtractor) {
        this.eventInfoExtractor = eventInfoExtractor;
    }
}
