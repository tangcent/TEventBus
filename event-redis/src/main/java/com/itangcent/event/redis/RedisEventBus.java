package com.itangcent.event.redis;

import com.itangcent.event.*;
import com.itangcent.event.serializer.JacksonSerializer;
import com.itangcent.event.serializer.Serializer;
import com.itangcent.event.subscriber.Subscriber;
import com.itangcent.event.utils.Runs;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.Pool;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RedisEventBus extends AbstractEventBus {
    private SubscriberRegistry subscriberRegistry;
    private Dispatcher dispatcher;
    private SubscriberExceptionHandler subscriberExceptionHandler;
    private Pool<Jedis> jedisPool;
    private static final Serializer serializer = new JacksonSerializer();

    public RedisEventBus(Pool<Jedis> jedisPool) {
        this(new DefaultSubscriberRegistry(),
                new ExecutorDispatcher(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1)),
                jedisPool);
    }

    public RedisEventBus(ExecutorService executorService,  Pool<Jedis> jedisPool) {
        this(new DefaultSubscriberRegistry(), new ExecutorDispatcher(executorService), jedisPool);
    }

    public RedisEventBus(SubscriberRegistry subscriberRegistry, ExecutorService executorService,  Pool<Jedis> jedisPool) {
        this(subscriberRegistry, new ExecutorDispatcher(executorService), jedisPool);
    }

    public RedisEventBus(SubscriberRegistry subscriberRegistry, Dispatcher dispatcher,  Pool<Jedis> jedisPool) {
        this.subscriberRegistry = subscriberRegistry;
        this.dispatcher = dispatcher;
        this.jedisPool = jedisPool;
        subscriberRegistry.listen(new RedisSubscribeListener());
        subscriberRegistry.findAllSubscribers(subscribers::add);
        flushListener();
    }

    public void setSubscriberExceptionHandler(SubscriberExceptionHandler subscriberExceptionHandler) {
        this.subscriberExceptionHandler = subscriberExceptionHandler;
    }

    //handle the register or unRegister
    private ListenThread listenThread;

    private Set<Subscriber> subscribers = new HashSet<>();

    protected class RedisSubscribeListener implements SubscribeListener {

        @Override
        public void onRegisterSubscriber(Subscriber subscriber) {
            lock.lock();
            try {
                subscribers.add(subscriber);
            } catch (Throwable e) {
                System.out.println(e.getMessage());
            } finally {
                lock.unlock();
            }
            flushListener();
        }

        @Override
        public void onUnRegisterSubscriber(Subscriber subscriber) {
            lock.lock();
            try {
                subscribers.remove(subscriber);
            } finally {
                lock.unlock();
            }
            flushListener();
        }
    }

    private byte[][] getListenPatterns() {
        lock.lock();
        try {
            return subscribers.stream()
                    .map(subscriber -> subscriber.getEventType().getName())
                    .distinct()
                    .map(serializer::serialize)
                    .toArray(byte[][]::new);
        } finally {
            lock.unlock();
        }
    }

    private void flushListener() {
        lock.lock();
        try {
            byte[][] listenPatterns = getListenPatterns();
            if (listenThread == null) {
                if (listenPatterns.length != 0) {
                    listenThread = new ListenThread(listenPatterns);
                    listenThread.start();
                    listenThread.waitSubscribe();
                }
            } else {
                if (listenPatterns.length == 0) {
                    listenThread.unsubscribe();
                    listenThread = null;
                } else {
                    listenThread.subscribe(listenPatterns);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private Lock lock = new ReentrantLock();

    private class ListenThread extends Thread {

        private Lock lock = new ReentrantLock();
        volatile byte[][] listenPatterns;

        public ListenThread(byte[][] listenPatterns) {
            this.listenPatterns = listenPatterns;
        }

        public void subscribe(byte[]... channels) {
            lock.lock();
            try {
                if (pubSub != null) {
                    //todo:equals without index
                    if (Arrays.deepEquals(channels, listenPatterns)) {
                        return;
                    }
                    listenPatterns = channels;
                    pubSub.subscribe(channels);
                }
            } catch (Exception e) {
                System.out.println("error");
            } finally {
                lock.unlock();
            }
        }

        volatile BinaryJedisPubSub pubSub;

        public void unsubscribe() {
            lock.lock();
            try {
                pubSub.unsubscribe();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void run() {
            pubSub = new BinaryJedisPubSub() {
                @Override
                public void onMessage(byte[] channel, byte[] message) {

                    RedisEventBus.this.onSubscribe(serializer.deserialize(message));
//                    if (channel == null) {
//                        RedisEventBus.this.onSubscribe(serializer.deserialize(message));
//                    } else {
//                        RedisEventBus.this.onSubscribe(
//                                new TopicEvent(serializer.deserialize(message), (String) serializer.deserialize(channel))
//                        );
//                    }
                }
            };
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.subscribe(pubSub, listenPatterns);
            }
        }

        public void waitSubscribe() {
            int i = 10;
            while (pubSub == null || pubSub.isSubscribed() && i-- > 0) {
                Runs.uncheckDo(() -> Thread.sleep(100));
            }
        }
    }

    @Override
    protected SubscriberRegistry getSubscriberRegistry() {
        return subscriberRegistry;
    }

    public void register(Object subscriber) {
        subscriberRegistry.register(subscriber);
    }

    public void unregister(Object subscriber) {
        subscriberRegistry.unregister(subscriber);
    }

    public void shutdown() {
        lock.lock();
        try {
            if (this.listenThread != null) {
                this.listenThread.unsubscribe();
                this.listenThread = null;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected Dispatcher getDispatcher() {
        return dispatcher;
    }

    @Override
    protected SubscriberExceptionHandler getSubscriberExceptionHandler() {
        return subscriberExceptionHandler;
    }

    @Override
    public void post(Object event) {
        byte[] rawChannel;
        byte[] rawMessage;
        if (event instanceof TopicEvent) {
            rawChannel = serializer.serialize(((TopicEvent) event).getEvent().getClass().getName());
            rawMessage = serializer.serialize(event);
        } else {
            rawChannel = serializer.serialize(event.getClass().getName());
            rawMessage = serializer.serialize(event);
        }
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(rawChannel, rawMessage);
        }
    }
}
