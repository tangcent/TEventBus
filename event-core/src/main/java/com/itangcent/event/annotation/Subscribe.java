package com.itangcent.event.annotation;

import com.itangcent.event.EventBus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {

    String group() default "default";

    Class<? extends EventBus>[] on() default {};

    int concurrency() default -1;
}
