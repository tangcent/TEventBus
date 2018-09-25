package com.itangcent.event.spring.core;

import org.springframework.cglib.core.DefaultNamingPolicy;
import org.springframework.cglib.core.SpringNamingPolicy;

public class TEventNamingPolicy extends DefaultNamingPolicy {

    public static final SpringNamingPolicy INSTANCE = new SpringNamingPolicy();

    @Override
    protected String getTag() {
        return "ByTEventCGLIB";
    }

}
