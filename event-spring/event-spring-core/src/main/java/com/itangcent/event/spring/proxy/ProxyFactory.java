package com.itangcent.event.spring.proxy;

public interface ProxyFactory {

    /**
     * Create a new proxy object.
     * <p>Uses the target's default class loader (if necessary for proxy creation):
     * usually, the thread context class loader.
     *
     * @return the new proxy object (never {@code null})
     * @see Thread#getContextClassLoader()
     */
    public Object buildProxy(Object target);


    /**
     * Create a new proxy object.
     * <p>Uses the given class loader (if necessary for proxy creation).
     * {@code null} will simply be passed down and thus lead to the low-level
     * proxy facility's default, which is usually different from the default chosen
     * by the ProxyFactory implementation's {@link #buildProxy(Object)} method.
     *
     * @param classLoader the class loader to create the proxy with
     *                    (or {@code null} for the low-level proxy facility's default)
     * @return the new proxy object (never {@code null})
     */
    public Object buildProxy(Object target, ClassLoader classLoader);

}
