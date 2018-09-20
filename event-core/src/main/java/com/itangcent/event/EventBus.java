package com.itangcent.event;

/**
 * Dispatches events to listeners
 */
public interface EventBus {

    /**
     * Posts an event to all registered subscribers. This method will return successfully after the
     * event has been posted to all subscribers, and regardless of any exceptions thrown by
     * subscribers.
     *
     * @param event event to post.
     */
    public void post(Object event);

}
