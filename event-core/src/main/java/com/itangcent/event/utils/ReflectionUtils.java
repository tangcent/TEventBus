package com.itangcent.event.utils;

import java.lang.reflect.*;

public final class ReflectionUtils {

    /**
     * Make the given field accessible, explicitly setting it accessible if
     * necessary. The {@code setAccessible(true)} method is only called
     * when actually necessary, to avoid unnecessary conflicts with a JVM
     * SecurityManager (if active).
     *
     * @param field the field to make accessible
     * @see Field#setAccessible
     */
    @SuppressWarnings("deprecation")  // on JDK 9
    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * Make the given method accessible, explicitly setting it accessible if
     * necessary. The {@code setAccessible(true)} method is only called
     * when actually necessary, to avoid unnecessary conflicts with a JVM
     * SecurityManager (if active).
     *
     * @param method the method to make accessible
     * @see Method#setAccessible
     */
    @SuppressWarnings("deprecation")  // on JDK 9
    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) ||
                !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * Make the given constructor accessible, explicitly setting it accessible
     * if necessary. The {@code setAccessible(true)} method is only called
     * when actually necessary, to avoid unnecessary conflicts with a JVM
     * SecurityManager (if active).
     *
     * @param ctor the constructor to make accessible
     * @see Constructor#setAccessible
     */
    @SuppressWarnings("deprecation")  // on JDK 9
    public static void makeAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers()) ||
                !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
            ctor.setAccessible(true);
        }
    }

    /**
     * 根据调用的目标与方法生成唯一性签名
     *
     * @param object -调用的目标
     * @param method -调用的方法
     * @return -唯一性签名
     */
    public static String buildKey(Object object, Method method) {
        Assert.notNull(object);
        return new StringBuilder(object.getClass().getName()).append(".").append(buildMethod(method)).toString();
    }

    /**
     * 根据调用的目标与方法生成唯一性签名
     *
     * @param cls    -调用的目标类型
     * @param method -调用的方法
     * @return -唯一性签名
     */
    public static String buildKey(Class cls, Method method) {
        Assert.notNull(cls);
        return new StringBuilder(cls.getName()).append(".").append(buildMethod(method)).toString();
    }

    public static String buildSimpleKey(Object object, Method method) {
        Assert.notNull(object);
        return new StringBuilder(object.getClass().getName()).append(".").append(method.getName()).toString();
    }

    /**
     * 根据方法名称及参数生成签名:
     * 1:此签名在此方法当前类中具有唯一性
     * 2:不同类中的方法如果方法名与参数类型均相同,则生成的方法签名是相同的
     *
     * @param method -目标方法
     * @return -唯一性签名(相对于此方法当前类)
     */
    public static String buildMethod(Method method) {
        StringBuilder strbuilder = new StringBuilder(method.getName());
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes != null && parameterTypes.length > 0) {
            for (Class<?> parameterType : parameterTypes) {
                strbuilder.append("#").append(parameterType.getName());
            }
        }
        return strbuilder.toString();
    }

    public static <T> T newProxy(Class<T> interfaceType, InvocationHandler handler) {
        Assert.notNull(handler);
        Assert.isTrue(interfaceType.isInterface(), "%s is not an interface", interfaceType);
        Object object =
                Proxy.newProxyInstance(
                        interfaceType.getClassLoader(), new Class<?>[]{interfaceType}, handler);
        return interfaceType.cast(object);
    }

}
