package com.itangcent.event.local;

import com.itangcent.event.SubscriberExceptionContext;
import com.itangcent.event.SubscriberExceptionHandler;
import com.itangcent.event.annotation.Retry;
import com.itangcent.event.annotation.Subscribe;
import org.junit.jupiter.api.Test;

public class LocalEventBusTest {

    @Test
    void test() {
        LocalEventBus localEventBus = new LocalEventBus();
        localEventBus.setSubscriberExceptionHandler(new SubscriberExceptionHandler() {
            @Override
            public void handleException(Throwable exception, SubscriberExceptionContext context) {
                System.out.println("handle:[" + exception.getMessage() + "]");
            }
        });
        Subscriber subscriber = new Subscriber();
        localEventBus.register(subscriber);
        localEventBus.post("hello world!");
        localEventBus.post("hello Tom!");
        localEventBus.unregister(subscriber);
        localEventBus.post("don't hello Emily!");
    }

    private class Subscriber {

        int i = 0;

        @Retry(times = 3)
        @Subscribe
        private void listenString(String str) {
            if (i++ < 2) {
                throw new IllegalArgumentException("error:" + str);
            }
            System.out.println(str);
        }
    }
}
