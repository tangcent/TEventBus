package com.itangcent.event;

import com.itangcent.event.annotation.Subscribe;
import com.itangcent.event.subscriber.*;
import com.itangcent.event.utils.AnnotationUtils;
import com.itangcent.event.utils.ArrayUtils;
import com.itangcent.event.utils.Predicates;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;

public class DefaultSubscriberRegistry extends AbstractSubscriberRegistry {

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
            if (subscriber instanceof SubscriberFilter && !((SubscriberFilter) subscriber).canSubscribe(eventBus, topic, event)) {
                iterator.remove();
            }
        }
        return subscribers;
    }

    @Override
    protected Subscriber buildSubscriber(Object subscriberBean, SubscriberMethod subscriberMethod) {
        Subscribe subscribe = AnnotationUtils.getAnnotation(subscriberMethod.getMethod(), Subscribe.class);
        String[] topics = subscribe.topic();
        String[] subscribeEventBus = subscribe.on();
        Subscriber subscriber = new DelegateMethodSubscriber(subscriberBean, subscriberMethod.getMethod(), subscriberMethod.getEventType());

        if (subscribe.concurrency() > 0) {
            subscriber = new ConcurrencySubscriber(subscriber, subscribe.concurrency());
        }

        if (!ArrayUtils.isEmpty(topics) || !ArrayUtils.isEmpty(subscribeEventBus)) {
            SelectSubscriberFilter selectSubscriberFilter = new SelectSubscriberFilter();
            if (!ArrayUtils.isEmpty(topics)) {
                selectSubscriberFilter.setTopicFilter(topicSelect(topics));
            }
            if (!ArrayUtils.isEmpty(subscribeEventBus)) {
                selectSubscriberFilter.setEventBusFilter(eventBusSelect(subscribeEventBus));
            }
            subscriber = new FilteredSubscriber(subscriber, selectSubscriberFilter);
        }


        return subscriber;
    }

    private static class SelectSubscriberFilter implements SubscriberFilter {
        protected Predicate<EventBus> eventBusFilter = Predicates.alwaysTrue();

        protected Predicate<String> topicFilter = Predicates.alwaysTrue();

        public void setEventBusFilter(Predicate<EventBus> eventBusFilter) {
            this.eventBusFilter = eventBusFilter;
        }

        public void setTopicFilter(Predicate<String> topicFilter) {
            this.topicFilter = topicFilter;
        }

        @Override
        public boolean canSubscribe(EventBus eventBus, String topic, Object event) {
            return eventBusFilter.test(eventBus) && topicFilter.test(topic);
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
