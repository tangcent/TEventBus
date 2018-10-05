package com.itangcent.event.spring.core.interceptor;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

public class EventBeanFactoryAdvisor extends AbstractBeanFactoryPointcutAdvisor {
    private EventPointcut eventPointcut = new EventPointcut();

    public void setEventInfoExtractor(EventInfoExtractor eventInfoExtractor) {
        this.eventPointcut.setEventInfoExtractor(eventInfoExtractor);
    }

    @Override
    public Pointcut getPointcut() {
        return eventPointcut;
    }
}
