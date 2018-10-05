package com.itangcent.event.spring.core.interceptor;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.lang.Nullable;

public class EventAspectSupport implements BeanFactoryAware, InitializingBean, SmartInitializingSingleton {

    @Nullable
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(@Nullable BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void afterSingletonsInstantiated() {

    }
}
