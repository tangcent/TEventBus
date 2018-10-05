package com.itangcent.event.spring.core.interceptor;

import com.itangcent.event.annotation.Stage;

public class EventPublishInfo extends EventInfo {

    /**
     * topics to publish
     */
    private String[] topic;

    /**
     * the bean name of EventBus which to publish to
     */
    private String[] to;

    //the event to post
    private String event;

    private Stage stage;

    /**
     * spring Expression Language (SpEL) expression used for making the event
     * publishing conditional.
     */
    private String condition;

    /**
     * spring Expression Language (SpEL) expression used  to veto event
     * publishing
     */
    private String unless;

    public String[] getTopic() {
        return topic;
    }

    public void setTopic(String[] topic) {
        this.topic = topic;
    }

    public String[] getTo() {
        return to;
    }

    public void setTo(String[] to) {
        this.to = to;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getUnless() {
        return unless;
    }

    public void setUnless(String unless) {
        this.unless = unless;
    }
}
