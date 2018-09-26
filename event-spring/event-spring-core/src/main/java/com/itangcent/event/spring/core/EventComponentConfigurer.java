package com.itangcent.event.spring.core;

import com.itangcent.event.SubscriberRegistry;

import java.util.ArrayList;
import java.util.List;

public class EventComponentConfigurer {

    private List<SubscriberRegistry> subscriberRegistries = new ArrayList<>();

    EventComponentConfigurer addAutoRegistry(SubscriberRegistry subscriberRegistry) {
        this.subscriberRegistries.add(subscriberRegistry);
        return this;
    }

    List<SubscriberRegistry> getSubscriberRegistries() {
        return subscriberRegistries;
    }
}
