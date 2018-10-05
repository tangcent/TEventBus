package com.itangcent.event.annotation;

import java.lang.annotation.*;

/**
 * publish the return value of the method to EventBus
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Publishes.class)
public @interface Publish {

    /**
     * topics to publish
     */
    String[] topic() default {};

    /**
     * the bean name of EventBus which to publish to
     */
    String[] to() default {};

    //the event to post
    String event() default "#result";

    Stage stage() default Stage.AFTER;

    /**
     * spring Expression Language (SpEL) expression used for making the event
     * publishing conditional.
     */
    String condition() default "";

    /**
     * spring Expression Language (SpEL) expression used  to veto event
     * publishing
     */
    String unless() default "";
}
