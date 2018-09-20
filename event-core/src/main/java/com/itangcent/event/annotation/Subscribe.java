package com.itangcent.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {

    String[] topic() default {};

    String group() default "default";

    /**
     * the bean name of EventBus which to subscribe on
     */
    String[] on() default {};

    /**
     * Sets the concurrency level that will be used by dispatcher
     */
    int concurrency() default -1;
}
