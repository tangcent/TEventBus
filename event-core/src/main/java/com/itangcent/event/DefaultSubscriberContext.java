package com.itangcent.event;

public class DefaultSubscriberContext implements SubscriberContext {
    private Object event;
    private Subscriber subscriber;
    private EventBus eventBus;

    public DefaultSubscriberContext(Object event, Subscriber subscriber, EventBus eventBus) {
        this.event = event;
        this.subscriber = subscriber;
        this.eventBus = eventBus;
    }

    @Override
    public Object getEvent() {
        return event;
    }

    @Override
    public Subscriber getSubscriber() {
        return subscriber;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }
}
