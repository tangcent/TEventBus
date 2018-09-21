package com.itangcent.event;

import com.itangcent.event.annotation.Subscribe;
import com.itangcent.event.utils.AnnotationUtils;
import com.itangcent.event.utils.ArrayUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class AbstractFilteredSubscriberRegistry extends AbstractSubscriberRegistry {

    @Override
    public Collection<Subscriber> getSubscribers(EventBus eventBus, Object event) {
        String topic = null;
        if (event instanceof TopicEvent) {
            topic = ((TopicEvent) event).getTopic();
            event = ((TopicEvent) event).getEvent();
        }
        Collection<Subscriber> subscribers = super.getSubscribers(eventBus, event);
        for (Iterator<Subscriber> iterator = subscribers.iterator(); iterator.hasNext(); ) {
            Subscriber subscriber = iterator.next();
            if (subscriber instanceof FilteredSubscriber && !((FilteredSubscriber) subscriber).canSubscribe(eventBus, topic, event)) {
                iterator.remove();
            }
        }
        return subscribers;
    }

    @Override
    protected Subscriber buildSubscriber(Object subscriber, SubscriberMethod subscriberMethod) {
        Subscribe subscribe = AnnotationUtils.getAnnotation(subscriberMethod.getMethod(), Subscribe.class);
        String[] topics = subscribe.topic();
        String[] subscribeEventBus = subscribe.on();
        if (ArrayUtils.isEmpty(topics) && ArrayUtils.isEmpty(subscribeEventBus)) {
            return new DelegateMethodSubscriber(subscriber, subscriberMethod.getMethod(), subscriberMethod.getEventType());
        } else {
            DefaultFilteredSubscriber filteredSubscriber = new DefaultFilteredSubscriber(subscriber, subscriberMethod.getMethod(), subscriberMethod.getEventType());
            if (!ArrayUtils.isEmpty(topics)) {
                filteredSubscriber.setTopicFilter(topicSelect(topics));
            }
            if (!ArrayUtils.isEmpty(subscribeEventBus)) {
                filteredSubscriber.setEventBusFilter(eventBusSelect(subscribeEventBus));
            }
            return filteredSubscriber;
        }
    }

    //todo:support pattern eventBus like [local*]
    protected Predicate<EventBus> eventBusSelect(String[] subscribeEventBus) {
        return eventBus -> {
            for (String bus : subscribeEventBus) {
                if (Objects.equals(bus, eventBus.name())) {
                    return true;
                }
            }
            return false;
        };
    }

    //todo:support pattern topics like [delete*]
    protected Predicate<String> topicSelect(String[] selectedTopics) {
        return topic -> {
            for (String selectedTopic : selectedTopics) {
                if (Objects.equals(topic, selectedTopic)) {
                    return true;
                }
            }
            return false;
        };
    }
}
