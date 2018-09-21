package com.itangcent.event;

public class ImmediateDispatcher extends AbstractDispatcher {
    private static final ImmediateDispatcher INSTANCE = new ImmediateDispatcher();

    private ImmediateDispatcher() {
    }

    public static ImmediateDispatcher instance() {
        return INSTANCE;
    }
}
