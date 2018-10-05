package com.itangcent.event.spring.core.interceptor;

import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.Collection;

public interface EventInfoExtractor {

    Collection<EventInfo> extract(Method method, @Nullable Class<?> targetClass);
}
