package com.itangcent.event.spring;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tevent")
public class TEventProperties {

    private boolean localEvent = true;

    private int localThread = -1;

    private boolean autoRegistry = true;

    public boolean isLocalEvent() {
        return localEvent;
    }

    public void setLocalEvent(boolean localEvent) {
        this.localEvent = localEvent;
    }

    public int getLocalThread() {
        return localThread;
    }

    public void setLocalThread(int localThread) {
        this.localThread = localThread;
    }

    public boolean isAutoRegistry() {
        return autoRegistry;
    }

    public void setAutoRegistry(boolean autoRegistry) {
        this.autoRegistry = autoRegistry;
    }
}
