package com.itangcent.event.spring.core.interceptor;

import com.itangcent.event.annotation.Publish;
import com.itangcent.event.annotation.Publishes;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultEventInfoExtractor implements EventInfoExtractor {

    private static final Collection<EventInfo> NULL = Collections.emptyList();
    private final Map<String, Collection<EventInfo>> eventInfoCache = new ConcurrentHashMap<>(1024);

    @Override
    public Collection<EventInfo> extract(Method method, Class<?> targetClass) {
        if (method.getDeclaringClass() == Object.class) {
            return null;
        }

        Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);

        String key = com.itangcent.event.utils.ReflectionUtils.buildKey(targetClass, specificMethod);
        Collection<EventInfo> eventInfos = this.eventInfoCache.get(key);

        if (eventInfos != null) {
            return (eventInfos != NULL ? eventInfos : null);
        } else {
            eventInfos = computeEventInfos(specificMethod, targetClass);
            if (eventInfos != null) {
                this.eventInfoCache.put(key, eventInfos);
            } else {
                this.eventInfoCache.put(key, NULL);
            }
            return eventInfos;
        }
    }

    private Collection<EventInfo> computeEventInfos(Method method, Class<?> targetClass) {
        Publishes publishesAnn = AnnotationUtils.findAnnotation(method, Publishes.class);
        if (publishesAnn != null) {
            Publish[] publishes = publishesAnn.value();
            List<EventInfo> eventInfos = new ArrayList<>(publishes.length);
            for (Publish publish : publishes) {
                eventInfos.add(extractInfo(publish));
            }
            return eventInfos;
        }
        Publish publish = AnnotationUtils.findAnnotation(method, Publish.class);

        if (publish != null) {
            return Collections.singletonList(extractInfo(publish));
        }

        return null;
    }

    private EventInfo extractInfo(Publish publish) {
        EventPublishInfo eventPublishInfo = new EventPublishInfo();
        eventPublishInfo.setCondition(publish.condition());
        eventPublishInfo.setEvent(publish.event());
        eventPublishInfo.setStage(publish.stage());
        eventPublishInfo.setTo(publish.to());
        eventPublishInfo.setUnless(publish.unless());
        eventPublishInfo.setTopic(publish.topic());
        return eventPublishInfo;
    }
}
