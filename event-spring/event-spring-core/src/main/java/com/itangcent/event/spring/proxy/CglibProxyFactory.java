package com.itangcent.event.spring.proxy;

import com.itangcent.event.spring.utils.ReflectionUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.cglib.core.NamingPolicy;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Map;

public abstract class CglibProxyFactory implements ProxyFactory {

    @Override
    public Object buildProxy(Object target) {
        final Class rootClass = AopProxyUtils.ultimateTargetClass(target);
        return buildProxy(target, rootClass, rootClass.getClassLoader());
    }

    @Override
    public Object buildProxy(Object target, ClassLoader classLoader) {
        final Class rootClass = AopProxyUtils.ultimateTargetClass(target);
        return buildProxy(target, rootClass, classLoader);
    }

    private Object buildProxy(Object target, Class rootClass, ClassLoader classLoader) {
        Object proxyObject = null;

        Assert.notNull(rootClass, "Target class must be available for creating a CGLIB proxy");

        Map<String, MethodInterceptor> proxyMethodMap = getProxyMethod(target, rootClass);
        //生成对应的CglibProxyMethodInterceptor,生产一个目标代理
        if (!CollectionUtils.isEmpty(proxyMethodMap)) {
            Class<?>[] interfaces = ReflectionUtils.proxiedUserInterfaces(target);
            Enhancer enhancer = createEnhancer();
            enhancer.setInterfaces(interfaces);
            enhancer.setSuperclass(rootClass);
            enhancer.setClassLoader(classLoader);
            enhancer.setCallbackType(CglibProxyMethodInterceptor.class);
            enhancer.setNamingPolicy(namePolicy());
            Class proxyClass = enhancer.createClass();
            Enhancer.registerCallbacks(proxyClass, new Callback[]{new CglibProxyMethodInterceptor(target, proxyMethodMap)});
            try {
                proxyObject = ReflectionUtils.newInstance(proxyClass);
            } finally {
                Enhancer.registerStaticCallbacks(proxyClass, null);
            }
        }
        return proxyObject == null ? target : proxyObject;
    }

    /**
     * Creates the CGLIB {@link Enhancer}. Subclasses may wish to override this to return a custom
     * {@link Enhancer} implementation.
     */
    protected Enhancer createEnhancer() {
        return new Enhancer();
    }

    /**
     * 生成类中需要代理的方法
     */
    protected abstract Map<String, MethodInterceptor> getProxyMethod(Object target, Class rootClass);

    protected abstract NamingPolicy namePolicy();
}
