package com.itangcent.event;

import com.itangcent.event.utils.Predicates;

import java.lang.reflect.Method;
import java.util.function.Predicate;

public class DefaultFilteredSubscriber extends DelegateMethodSubscriber implements FilteredSubscriber {
    private Predicate<EventBus> eventBusFilter = Predicates.alwaysTrue();
    private Predicate<String> topicFilter = Predicates.alwaysTrue();

    public void setEventBusFilter(Predicate<EventBus> eventBusFilter) {
        this.eventBusFilter = eventBusFilter;
    }

    public void setTopicFilter(Predicate<String> topicFilter) {
        this.topicFilter = topicFilter;
    }

    public DefaultFilteredSubscriber(Object delegate, Method method, Class eventType) {
        super(delegate, method, eventType);
    }

    @Override
    public boolean canSubscribe(EventBus eventBus, String topic, Object event) {
        return eventBusFilter.test(eventBus) & topicFilter.test(topic);
    }
}
