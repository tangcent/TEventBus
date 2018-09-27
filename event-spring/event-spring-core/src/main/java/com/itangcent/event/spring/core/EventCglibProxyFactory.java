package com.itangcent.event.spring.core;

import com.itangcent.event.EventBus;
import com.itangcent.event.annotation.Publish;
import com.itangcent.event.reflect.TypeToken;
import com.itangcent.event.spring.proxy.CglibProxyFactory;
import com.itangcent.event.utils.AnnotationUtils;
import com.itangcent.event.utils.Assert;
import com.itangcent.event.utils.Collections;
import com.itangcent.event.utils.ReflectionUtils;
import org.springframework.cglib.core.NamingPolicy;
import org.springframework.cglib.proxy.MethodInterceptor;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.*;

public class EventCglibProxyFactory extends CglibProxyFactory {

    @Resource
    private EventBusManager eventBusManager;

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, MethodInterceptor> getProxyMethod(Object target, Class rootClass) {

        Set<Class> supertypes = TypeToken.of(rootClass).getTypes().rawTypes();
        Map<String, MethodInterceptor> proxyMethodMap = new HashMap<>();
        for (Class<?> supertype : supertypes) {
            for (Method method : supertype.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Publish.class) && !method.isSynthetic()) {
                    Class<?> returnType = method.getReturnType();
                    Assert.isTrue(
                            returnType != Void.TYPE,
                            "Method %s has @Publish annotation but return void.",
                            com.itangcent.event.utils.ReflectionUtils.buildKey(rootClass, method));

                    proxyMethodMap.put(ReflectionUtils.buildMethod(method), getOrCreateProxyMethod(target, method));
                }
            }
        }
        return proxyMethodMap;
    }

    private MethodInterceptor getOrCreateProxyMethod(Object target, Method method) {

        Publish publish = AnnotationUtils.getAnnotation(method, Publish.class);

        EventBus toEventBus = null;

        String[] to = publish.to();
        if (to.length == 0) {
            toEventBus = eventBusManager.eventBuses();
        } else {
            if (to.length == 1) {
                toEventBus = eventBusManager.getEventBus(to[0]);
            } else {
                EventBus[] toEventBuses = new EventBus[to.length];
                for (int i = 0; i < to.length; i++) {
                    toEventBuses[i] = eventBusManager.getEventBus(to[i]);
                }
                toEventBus = new ComponentEventBus(toEventBuses);
            }
        }

        String[] topics = publish.topic();
        if (topics.length == 0) {
            return buildOrCreateProxyMethod(toEventBus, null);
        } else {
            return buildOrCreateProxyMethod(toEventBus, topics);
        }
    }

    private List<MethodInterceptor> publishProxyMethodCache = new ArrayList<>();

    private MethodInterceptor buildOrCreateProxyMethod(EventBus toEventBus, String[] topics) {
        if (topics == null) {
            PublishProxyMethod publishProxyMethod = publishProxyMethodCache.stream()
                    .filter(mi -> mi instanceof PublishProxyMethod)
                    .map(mi -> (PublishProxyMethod) mi)
                    .filter(pm -> pm.getEventBus() == toEventBus)
                    .findFirst()
                    .orElse(null);
            if (publishProxyMethod == null) {
                publishProxyMethod = new PublishProxyMethod(toEventBus);
                publishProxyMethodCache.add(publishProxyMethod);
            }
            return publishProxyMethod;
        } else {
            TopicPublishProxyMethod topicPublishProxyMethod = publishProxyMethodCache.stream()
                    .filter(mi -> mi instanceof TopicPublishProxyMethod)
                    .map(mi -> (TopicPublishProxyMethod) mi)
                    .filter(pm -> pm.getEventBus() == toEventBus && Collections.equals(pm.getTopics(), topics))
                    .findFirst()
                    .orElse(null);
            if (topicPublishProxyMethod == null) {
                topicPublishProxyMethod = new TopicPublishProxyMethod(toEventBus, topics);
                publishProxyMethodCache.add(topicPublishProxyMethod);
            }
            return topicPublishProxyMethod;
        }
    }

    @Override
    protected NamingPolicy namePolicy() {
        return TEventNamingPolicy.INSTANCE;
    }
}
