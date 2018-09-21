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
        localEventBus.post("world", "newUser");
        localEventBus.post("Tom", "oldUser");
        localEventBus.post("Louis");
        localEventBus.unregister(subscriber);
        localEventBus.post("Emily");
    }

    private class Subscriber {

        int i = 0;

        @Subscribe
        private void listenUser(String name) {
            System.out.println(name + " login");
        }

        @Retry(times = 3)
        @Subscribe(topic = "newUser")
        private void listenNewUser(String name) {
            if (i++ < 2) {
                throw new IllegalArgumentException("error hello new user:" + name);
            }
            System.out.println("hello " + name + "， welcome here");
        }

        @Retry(times = 3)
        @Subscribe(topic = "oldUser")
        private void listenOldUser(String name) {
            if (i++ < 2) {
                throw new IllegalArgumentException("error hello new user:" + name);
            }
            System.out.println("hello " + name + "， welcome back");
        }
    }
}
