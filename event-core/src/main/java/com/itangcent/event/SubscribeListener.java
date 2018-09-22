package com.itangcent.event;

import com.itangcent.event.subscriber.Subscriber;

import java.util.ArrayList;
import java.util.List;

public interface SubscribeListener {
    void onRegisterSubscriber(Subscriber subscriber);

    void onUnRegisterSubscriber(Subscriber subscriber);

    public static SubscribeListener union(SubscribeListener one, SubscribeListener other) {
        List<SubscribeListener> subscribeListeners = new ArrayList<>();
        if (one instanceof ComponentSubscribeListener) {
            java.util.Collections.addAll(subscribeListeners, ((ComponentSubscribeListener) one).subscribeListeners);
        } else {
            subscribeListeners.add(one);
        }
        if (other instanceof ComponentSubscribeListener) {
            java.util.Collections.addAll(subscribeListeners, ((ComponentSubscribeListener) other).subscribeListeners);
        } else {
            subscribeListeners.add(other);
        }
        return new ComponentSubscribeListener(subscribeListeners.toArray(new SubscribeListener[subscribeListeners.size()]));
    }

    public static class ComponentSubscribeListener implements SubscribeListener {
        SubscribeListener[] subscribeListeners;

        public ComponentSubscribeListener(SubscribeListener[] subscribeListeners) {
            this.subscribeListeners = subscribeListeners;
        }

        @Override
        public void onRegisterSubscriber(Subscriber subscriber) {
            for (SubscribeListener subscribeListener : subscribeListeners) {
                subscribeListener.onRegisterSubscriber(subscriber);
            }
        }

        @Override
        public void onUnRegisterSubscriber(Subscriber subscriber) {
            for (SubscribeListener subscribeListener : subscribeListeners) {
                subscribeListener.onUnRegisterSubscriber(subscriber);
            }
        }
    }
}
