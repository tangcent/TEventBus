package com.itangcent.event;

public interface FilteredSubscriber extends Subscriber {
    boolean canSubscribe(final EventBus eventBus, final String topic, final Object event);
}
