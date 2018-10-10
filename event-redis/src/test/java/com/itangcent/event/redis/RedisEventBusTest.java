package com.itangcent.event.redis;

import com.itangcent.event.LoggedEventExceptionHandle;
import com.itangcent.event.annotation.Retry;
import com.itangcent.event.annotation.Subscribe;
import com.itangcent.event.utils.Runs;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisPool;

import java.text.MessageFormat;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RedisEventBusTest {

    public static final String[] oldUsers = new String[]{"Tom", "William", "Wilbert", "David", "Sofia"};
    public static final String[] newUsers = new String[]{"Louis", "Lily", "Taylor", "Jake", "Gavin"};

    @Test
    public void test() {
        //hostA subscribe Thread
        new Thread(() -> {
            RedisEventBus redisEventBus = new RedisEventBus(new JedisPool("localhost", 6379));
            redisEventBus.setSubscriberExceptionHandler(LoggedEventExceptionHandle.message());
            Subscriber subscriber = new Subscriber("hostA");
            redisEventBus.register(subscriber);
            Runs.uncheckDo(() -> Thread.sleep(TimeUnit.SECONDS.toMillis(10)));
//            redisEventBus.shutdown();
        }).start();

        //hostB subscribe Thread
        new Thread(() -> {
            RedisEventBus redisEventBus = new RedisEventBus(new JedisPool("localhost", 6379));
            redisEventBus.setSubscriberExceptionHandler(LoggedEventExceptionHandle.message());
            Subscriber subscriber = new Subscriber("hostB");
            redisEventBus.register(subscriber);
            Runs.uncheckDo(() -> Thread.sleep(TimeUnit.SECONDS.toMillis(10)));
//            redisEventBus.shutdown();
        }).start();

        Runs.uncheckDo(() -> Thread.sleep(TimeUnit.SECONDS.toMillis(2)));

        //event post Thread
        new Thread(() -> {
            RedisEventBus redisEventBus = new RedisEventBus(new JedisPool("localhost", 6379));
            redisEventBus.setSubscriberExceptionHandler(LoggedEventExceptionHandle.message());
            redisEventBus.post("world", "newUser");
            for (String oldUser : oldUsers) {
                redisEventBus.post(oldUser, "oldUser");
            }
            for (String newUser : newUsers) {
                redisEventBus.post(newUser, "newUser");
            }
            redisEventBus.post("Jeremiah");
            redisEventBus.post("Emily");
            Runs.uncheckDo(() -> Thread.sleep(TimeUnit.SECONDS.toMillis(10)));
//            redisEventBus.shutdown();
        }).start();

        Runs.uncheckDo(() -> Thread.sleep(TimeUnit.SECONDS.toMillis(10)));
    }

    private class Subscriber {

        String host = "localhost";

        public Subscriber() {
        }

        public Subscriber(String host) {
            this.host = host;
        }

        Random random = new Random(System.currentTimeMillis());

        AtomicInteger i = new AtomicInteger(0);
        AtomicInteger newUserCount = new AtomicInteger(0);
        AtomicInteger oldUserCount = new AtomicInteger(0);

        @Subscribe
        private void listenString(String str, String topic) {
            System.out.println(MessageFormat.format("[{0}]A String Event:[{1}],topic:[{2}]", host, str, topic));
        }

        @Subscribe(topic = "*User")
        private void listenUser(String name, String topic) {
            assert topic.endsWith("User");
            System.out.println(MessageFormat.format("[{0}][{1}]{2} {3} login", host, i.getAndIncrement(), topic, name));
        }

        @Retry(times = 3)
        @Subscribe(topic = "newUser", concurrency = 2)
        private void listenNewUser(String name) {
            if (random.nextBoolean()) {
                throw new IllegalArgumentException("error hello new user:" + name);
            }
            int count = newUserCount.incrementAndGet();
            assert count < 3;
            System.out.println(MessageFormat.format("[{0}][{1}]hello {2}， welcome here", host, count, name));
            Runs.uncheckDo(() -> Thread.sleep(TimeUnit.MILLISECONDS.toMillis(500)));
            newUserCount.decrementAndGet();
        }

        @Retry(times = 3)
        @Subscribe(topic = "oldUser", concurrency = 3)
        private void listenOldUser(String name) {
            if (random.nextBoolean()) {
                throw new IllegalArgumentException("error hello old user:" + name);
            }
            int count = oldUserCount.incrementAndGet();
            assert count < 4;
            System.out.println(MessageFormat.format("[{0}][{1}]hello {2}， welcome back", host, count, name));
            Runs.uncheckDo(() -> Thread.sleep(TimeUnit.MILLISECONDS.toMillis(500)));
            oldUserCount.decrementAndGet();
        }
    }
}
