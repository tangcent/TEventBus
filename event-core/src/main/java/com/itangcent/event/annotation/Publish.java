package com.itangcent.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * publish the return value of the method to EventBus
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Publish {

    String[] topic() default {};

    /**
     * the bean name of EventBus which to publish to
     */
    String[] to() default {};

    /**
     * pring Expression Language (SpEL) expression used for making the event
     * publishing conditional.
     */
    String condition() default "";

    /**
     * pring Expression Language (SpEL) expression used  to veto event
     * publishing
     */
    String unless() default "";
}
