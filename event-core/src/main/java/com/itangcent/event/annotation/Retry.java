package com.itangcent.event.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Retry {

    /**
     * the type of exception which need retry
     */
    Class<? extends Throwable>[] on() default Throwable.class;

    /**
     * the max retry times
     */
    int times() default -1;
}
