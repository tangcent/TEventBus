package com.itangcent.event.spring.core;

import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

public class EventBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // Ensure an auto-proxy creator is registered.
        AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);

//        RootBeanDefinition beanDefinition = new RootBeanDefinition(EventBeanFactoryAdvisor.class);
//        // Bean will only be auto-proxied if it has infrastructure role.
//        beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
//        registry.registerBeanDefinition("eventBeanFactoryAdvisor", beanDefinition);

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}
