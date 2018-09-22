package com.itangcent.event.redis;

import com.itangcent.event.*;
import com.itangcent.event.serializer.JacksonSerializer;
import com.itangcent.event.serializer.Serializer;
import com.itangcent.event.subscriber.Subscriber;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RedisEventBus extends AbstractEventBus {
    private SubscriberRegistry subscriberRegistry;
    private Dispatcher dispatcher;
    private SubscriberExceptionHandler subscriberExceptionHandler;
    private String name = "RedisEventBus";
    private JedisPool jedisPool;
    private static final Serializer serializer = new JacksonSerializer();

    public RedisEventBus(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        subscriberRegistry = new DefaultSubscriberRegistry();
        dispatcher = ImmediateDispatcher.instance();
    }

    public RedisEventBus(ExecutorService executorService, JedisPool jedisPool) {
        this(new DefaultSubscriberRegistry(), new ExecutorDispatcher(executorService), jedisPool);
    }

    public RedisEventBus(SubscriberRegistry subscriberRegistry, Dispatcher dispatcher, JedisPool jedisPool) {
        this.subscriberRegistry = subscriberRegistry;
        this.dispatcher = dispatcher;
        this.jedisPool = jedisPool;
        subscriberRegistry.listen(new RedisSubscribeListener());
    }

    public void setSubscriberExceptionHandler(SubscriberExceptionHandler subscriberExceptionHandler) {
        this.subscriberExceptionHandler = subscriberExceptionHandler;
    }

    ListenThread listenThread;//handle the register or unRegister
//    Thread subscribeThread;//subscribe redis

    private List<Subscriber> subscribers = new ArrayList<>();

    protected class RedisSubscribeListener implements SubscribeListener {

        @Override
        public void onRegisterSubscriber(Subscriber subscriber) {
            lock.lock();
            try {
                subscribers.add(subscriber);
                flushListener();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void onUnRegisterSubscriber(Subscriber subscriber) {
            lock.lock();
            try {
                subscribers.remove(subscriber);
                flushListener();
            } finally {
                lock.unlock();
            }
        }
    }

    private byte[][] getListenPatterns() {
        return subscribers.stream()
                .map(subscriber -> subscriber.getEventType().getName())
                .map(serializer::serialize)
                .toArray(byte[][]::new);
    }

    private void flushListener() {
        lock.lock();
        try {
            if (listenThread == null) {
                listenThread = new ListenThread();
                listenThread.start();
            } else {
                byte[][] listenPatterns = getListenPatterns();
                if (listenPatterns == null || listenPatterns.length == 0) {
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

        public void subscribe(byte[]... channels) {
            lock.lock();
            try {
                pubSub.subscribe(channels);
            } finally {
                lock.unlock();
            }
        }

        BinaryJedisPubSub pubSub;

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
            lock.lock();
            try {
                byte[][] listenPatterns = getListenPatterns();
                pubSub = new BinaryJedisPubSub() {
                    @Override
                    public void onMessage(byte[] channel, byte[] message) {
                        if (channel == null) {
                            RedisEventBus.this.onSubscribe(serializer.deserialize(message));
                        } else {
                            RedisEventBus.this.onSubscribe(
                                    new TopicEvent(serializer.deserialize(message), (String) serializer.deserialize(channel))
                            );
                        }
                    }
                };
                try (Jedis jedis = jedisPool.getResource()) {
                    jedis.subscribe(pubSub, listenPatterns);
                }
            } finally {
                lock.unlock();
            }

        }
    }

    @Override
    protected SubscriberRegistry getSubscriberRegistry() {
        return subscriberRegistry;
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
    public String name() {
        return name;
    }

    @Override
    public void post(Object event) {
        byte[] rawChannel = serializer.serialize(event.getClass().getName());
        byte[] rawMessage = serializer.serialize(event);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(rawChannel, rawMessage);
        }
    }
}
