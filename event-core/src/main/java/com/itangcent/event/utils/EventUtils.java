package com.itangcent.event.utils;

import java.lang.reflect.Method;

public final class EventUtils {

    public static boolean isSuitableMethod(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();

        // check out the argument numbers
        return ((parameterTypes.length == 2 && String.class.equals(parameterTypes[1])) || parameterTypes.length == 1);
    }
}
