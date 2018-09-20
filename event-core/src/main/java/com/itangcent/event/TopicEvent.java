package com.itangcent.event;

public class TopicEvent {
    private Object event;

    private String topic;

    public TopicEvent() {
    }

    public TopicEvent(Object event, String topic) {
        this.event = event;
        this.topic = topic;
    }

    public Object getEvent() {
        return event;
    }

    public void setEvent(Object event) {
        this.event = event;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
