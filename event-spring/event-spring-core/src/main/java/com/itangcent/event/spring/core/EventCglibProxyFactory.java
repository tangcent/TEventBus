package com.itangcent.event.spring.core;

import com.itangcent.event.EventBus;
import com.itangcent.event.annotation.Publish;
import com.itangcent.event.reflect.TypeToken;
import com.itangcent.event.spring.proxy.CglibProxyFactory;
import com.itangcent.event.utils.AnnotationUtils;
import com.itangcent.event.utils.Assert;
import com.itangcent.event.utils.ReflectionUtils;
import org.springframework.cglib.core.NamingPolicy;
import org.springframework.cglib.proxy.MethodInterceptor;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

        EventBus[] toEventBus = null;

        String[] to = publish.to();
        if (to.length == 0) {
            Collection<EventBus> collection = eventBusManager.eventBuses();
            toEventBus = collection.toArray(new EventBus[collection.size()]);
        } else {
            toEventBus = new EventBus[to.length];
            for (int i = 0; i < to.length; i++) {
                toEventBus[i] = eventBusManager.getEventBus(to[i]);
            }
        }

        String[] topics = publish.topic();
        if (topics.length == 0) {
            return new PublishProxyMethod(toEventBus);
        } else {
            return new TopicPublishProxyMethod(topics, toEventBus);
        }
    }

    @Override
    protected NamingPolicy namePolicy() {
        return TEventNamingPolicy.INSTANCE;
    }
}
