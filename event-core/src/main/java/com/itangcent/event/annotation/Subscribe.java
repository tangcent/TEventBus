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
     * the name of EventBus which to subscribe on
     */
    String[] on() default {};

    /**
     * Sets the concurrency level that will be used by dispatcher
     */
    int concurrency() default -1;

    /**
     * support:
     * highest->Integer.MAX_VALUE/0x7fffffff/2147483647
     * lowest->Integer.MIN_VALUE/0x80000000/-2147483648
     * but [Thread.MIN_PRIORITY/Thread.NORM_PRIORITY/Thread.MAX_PRIORITY] is recommended instead
     */
    int priority() default Thread.NORM_PRIORITY;
}
