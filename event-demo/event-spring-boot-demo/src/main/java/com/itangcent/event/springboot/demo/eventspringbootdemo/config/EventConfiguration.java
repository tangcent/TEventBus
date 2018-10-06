package com.itangcent.event.springboot.demo.eventspringbootdemo.config;

import com.itangcent.event.EventBus;
import com.itangcent.event.SubscriberRegistry;
import com.itangcent.event.redis.RedisEventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.Executors;

@Configuration
public class EventConfiguration {

    @Bean
    public EventBus redisEventBus(SubscriberRegistry subscriberRegistry) {
        return new RedisEventBus(subscriberRegistry, Executors.newFixedThreadPool(2), new JedisPool("localhost", 6379));
    }
}
