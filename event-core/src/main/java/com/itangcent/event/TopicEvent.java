package com.itangcent.event;

import java.util.Objects;

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TopicEvent{");
        sb.append("event=[").append(event);
        sb.append("], topic=[").append(topic).append(']');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicEvent that = (TopicEvent) o;
        return Objects.equals(event, that.event) &&
                Objects.equals(topic, that.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, topic);
    }
}
