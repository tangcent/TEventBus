package com.itangcent.event.reflect;

import com.itangcent.event.utils.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public final class Parameter implements AnnotatedElement {

    private final Invokable<?, ?> declaration;
    private final int position;
    private final TypeToken<?> type;
    private final List<Annotation> annotations;
    private final AnnotatedType annotatedType;

    Parameter(
            Invokable<?, ?> declaration,
            int position,
            TypeToken<?> type,
            Annotation[] annotations,
            AnnotatedType annotatedType) {
        this.declaration = declaration;
        this.position = position;
        this.type = type;
        this.annotations = Arrays.asList(annotations);
        this.annotatedType = annotatedType;
    }

    /**
     * Returns the type of the parameter.
     */
    public TypeToken<?> getType() {
        return type;
    }

    /**
     * Returns the {@link Invokable} that declares this parameter.
     */
    public Invokable<?, ?> getDeclaringInvokable() {
        return declaration;
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return getAnnotation(annotationType) != null;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        Assert.notNull(annotationType);
        for (Annotation annotation : annotations) {
            if (annotationType.isInstance(annotation)) {
                return annotationType.cast(annotation);
            }
        }
        return null;
    }

    @Override
    public Annotation[] getAnnotations() {
        return getDeclaredAnnotations();
    }

    /**
     * @since 18.0
     */
    // @Override on JDK8
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        return getDeclaredAnnotationsByType(annotationType);
    }

    /**
     * @since 18.0
     */
    // @Override on JDK8
    @Override
    public Annotation[] getDeclaredAnnotations() {
        return annotations.toArray(new Annotation[annotations.size()]);
    }

    /**
     * @since 18.0
     */
    // @Override on JDK8
    public <A extends Annotation> A getDeclaredAnnotation(Class<A> annotationType) {
        Assert.notNull(annotationType);
        Annotation annotation = annotations.stream().filter(annotationType::isInstance).findFirst().orElse(null);
        return (A) annotation;
    }

    /**
     * @since 18.0
     */
    // @Override on JDK8
    public <A extends Annotation> A[] getDeclaredAnnotationsByType(Class<A> annotationType) {
        return annotations.stream().filter(annotationType::isInstance)
                .toArray(i -> (A[]) Array.newInstance(annotationType, i));
    }

    /**
     * @since 25.1
     */
    // @Override on JDK8
    public AnnotatedType getAnnotatedType() {
        return annotatedType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Parameter) {
            Parameter that = (Parameter) obj;
            return position == that.position && declaration.equals(that.declaration);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return position;
    }

    @Override
    public String toString() {
        return type + " arg" + position;
    }
}
