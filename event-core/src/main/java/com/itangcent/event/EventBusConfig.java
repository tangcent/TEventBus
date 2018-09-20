package com.itangcent.event;

import java.util.concurrent.Executor;

public interface EventBusConfig {
    void setExecutor(Executor executor);

    void setDispatcher(Dispatcher dispatcher);
}
