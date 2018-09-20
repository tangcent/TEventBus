package com.itangcent.event.reflect;

import com.itangcent.event.utils.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

abstract class TypeCapture<T> {

    /**
     * Returns the captured type.
     */
    final Type capture() {
        Type superclass = getClass().getGenericSuperclass();
        Assert.isInstanceOf(ParameterizedType.class, superclass, "%s isn't parameterized", superclass);
        return ((ParameterizedType) superclass).getActualTypeArguments()[0];
    }
}