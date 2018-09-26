package com.itangcent.event.spring.core;

import com.itangcent.event.EventBus;
import com.itangcent.event.annotation.Publish;
import com.itangcent.event.annotation.Subscribe;
import com.itangcent.event.spring.utils.ReflectionUtils;
import com.itangcent.event.utils.AnnotationUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.annotation.Resource;

public class EventBeanPostProcessor implements BeanPostProcessor {

    @Resource
    EventCglibProxyFactory eventCglibProxyFactory;

    @Resource
    EventBusManager eventBusManager;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof EventBus) {
            eventBusManager.addEventBus(beanName, (EventBus) bean);
        }

        if (bean instanceof EventComponentConfigurer) {
            eventBusManager.setSubscriberRegistries(((EventComponentConfigurer) bean).getSubscriberRegistries());
        }

        Class<?> beanCls = ReflectionUtils.getClass(bean);

        if (AnnotationUtils.existedAnnotationAnyWhere(beanCls, Subscribe.class)) {
            eventBusManager.addEventBusListeners(bean);
        }

        if (AnnotationUtils.existedAnnotationAnyWhere(beanCls, Publish.class)) {
            return eventCglibProxyFactory.buildProxy(bean);
        }

        return bean;
    }
}
