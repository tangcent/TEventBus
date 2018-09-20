package com.itangcent.event.reflect;

import com.itangcent.event.utils.Assert;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public abstract class TypeParameter<T> extends TypeCapture<T> {

    final TypeVariable<?> typeVariable;

    protected TypeParameter() {
        Type type = capture();
        Assert.isInstanceOf(TypeVariable.class, type, "%s should be a type variable.", type);
        this.typeVariable = (TypeVariable<?>) type;
    }

    @Override
    public final int hashCode() {
        return typeVariable.hashCode();
    }

    @Override
    public final boolean equals(Object o) {
        if (o instanceof TypeParameter) {
            TypeParameter<?> that = (TypeParameter<?>) o;
            return typeVariable.equals(that.typeVariable);
        }
        return false;
    }

    @Override
    public String toString() {
        return typeVariable.toString();
    }
}
