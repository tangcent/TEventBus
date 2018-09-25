package com.itangcent.event.spring.proxy;

import com.itangcent.event.utils.ReflectionUtils;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;

public class CglibProxyMethodInterceptor implements MethodInterceptor {

    private final Object targetObject;

    private final Map<String, MethodInterceptor> proxyMethod;

    public CglibProxyMethodInterceptor(Object targetObject, Map<String, MethodInterceptor> proxyMethod) {
        this.targetObject = targetObject;
        this.proxyMethod = proxyMethod;
    }

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        String key = ReflectionUtils.buildMethod(method);
        if (proxyMethod.containsKey(key)) {
            //use proxyMethod if existed
            return proxyMethod.get(key).intercept(targetObject, method, objects, methodProxy);
        } else {
            return method.invoke(targetObject, objects);
        }
    }
}

