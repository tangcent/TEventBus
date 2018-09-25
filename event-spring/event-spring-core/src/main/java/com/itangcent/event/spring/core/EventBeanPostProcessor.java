package com.itangcent.event.spring.core;

import com.itangcent.event.EventBus;
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
        //todo:consider only try build proxy for class which annotation by @Event
        return eventCglibProxyFactory.buildProxy(bean);
    }
}
