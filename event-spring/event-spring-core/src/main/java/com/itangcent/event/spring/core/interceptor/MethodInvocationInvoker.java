package com.itangcent.event.spring.core.interceptor;

import org.aopalliance.intercept.MethodInvocation;

public class MethodInvocationInvoker implements EventMethodInvoker {
    private MethodInvocation invocation;

    public MethodInvocationInvoker(MethodInvocation invocation) {
        this.invocation = invocation;
    }

    @Override
    public Object invoke() throws ThrowableWrapper {
        try {
            return invocation.proceed();
        } catch (Throwable throwable) {
            throw new EventMethodInvoker.ThrowableWrapper(throwable);
        }
    }
}
