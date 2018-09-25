package com.itangcent.event.spring.utils;

import com.itangcent.event.utils.Runs;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public class SpringBeanFactory implements BeanFactoryAware {
    private static BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        SpringBeanFactory.beanFactory = beanFactory;
    }

    /**
     * Return an instance, which may be shared or independent, of the specified bean.
     *
     * @param beanName the name of the bean to retrieve
     * @return an instance of the bean if the specified bean existed. otherwise null;
     */
    public static Object getBean(String beanName) {
        return Runs.safeCall(() -> beanFactory.getBean(beanName), null);
    }

    /**
     * Return the bean instance that uniquely matches the given object type, if any.
     *
     * @param clazz type the bean must match; can be an interface or superclass
     * @return an instance of the single bean matching the required type if the specified bean existed. otherwise null;
     */
    public static <T> T getBean(Class<T> clazz) {
        return Runs.safeCall(() -> beanFactory.getBean(clazz), null);
    }
}
