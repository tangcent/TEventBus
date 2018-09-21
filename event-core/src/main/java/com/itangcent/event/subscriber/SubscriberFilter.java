package com.itangcent.event.subscriber;

import com.itangcent.event.EventBus;

public interface SubscriberFilter {
    boolean canSubscribe(final EventBus eventBus, final String topic, final Object event);

    AlwaysTrueSubscriberFilter ALWAYS_TRUE_SUBSCRIBER_FILTER = new AlwaysTrueSubscriberFilter();

    public static SubscriberFilter alwaysTrue() {
        return ALWAYS_TRUE_SUBSCRIBER_FILTER;
    }

    public static class AlwaysTrueSubscriberFilter implements SubscriberFilter {
        @Override
        public boolean canSubscribe(EventBus eventBus, String topic, Object event) {
            return true;
        }
    }

    public default SubscriberFilter and(SubscriberFilter nextFilter) {
        return (eventBus, topic, event) -> canSubscribe(eventBus, topic, event) && nextFilter.canSubscribe(eventBus, topic, event);
    }
}
