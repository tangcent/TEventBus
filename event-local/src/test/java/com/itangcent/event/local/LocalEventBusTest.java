package com.itangcent.event.local;

import com.itangcent.event.LoggedEventExceptionHandle;
import com.itangcent.event.annotation.Retry;
import com.itangcent.event.annotation.Subscribe;
import com.itangcent.event.utils.Runs;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LocalEventBusTest {

    @Test
    void test() {
        LocalEventBus localEventBus = new LocalEventBus(Executors.newFixedThreadPool(2));
        localEventBus.setSubscriberExceptionHandler(LoggedEventExceptionHandle.message());
        Subscriber subscriber = new Subscriber();
        localEventBus.register(subscriber);
        localEventBus.post("world", "newUser");
        localEventBus.post("Tom", "oldUser");
        localEventBus.post("Louis");
        localEventBus.unregister(subscriber);
        localEventBus.post("Emily");
        Runs.uncheckDo(() -> Thread.sleep(TimeUnit.SECONDS.toMillis(1)));
    }

    private class Subscriber {

        AtomicInteger i = new AtomicInteger(0);

        @Subscribe
        private void listenUser(String name, String topic) {
            System.out.println(topic + " " + name + " login");
        }

        @Retry(times = 3)
        @Subscribe(topic = "newUser")
        private void listenNewUser(String name) {
            if (i.getAndIncrement() < 3) {
                throw new IllegalArgumentException("error hello new user:" + name);
            }
            System.out.println("hello " + name + "， welcome here");
        }

        @Retry(times = 3)
        @Subscribe(topic = "oldUser")
        private void listenOldUser(String name) {
            if (i.getAndIncrement() < 3) {
                throw new IllegalArgumentException("error hello old user:" + name);
            }
            System.out.println("hello " + name + "， welcome back");
        }
    }
}
