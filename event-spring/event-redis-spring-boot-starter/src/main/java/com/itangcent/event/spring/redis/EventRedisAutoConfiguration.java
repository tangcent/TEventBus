package com.itangcent.event.spring.redis;

import com.itangcent.event.EventBus;
import com.itangcent.event.ExecutorDispatcher;
import com.itangcent.event.SubscriberRegistry;
import com.itangcent.event.redis.RedisEventBus;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import redis.clients.jedis.*;
import redis.clients.util.Pool;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

@EnableConfigurationProperties({EventRedisProperties.class})
public class EventRedisAutoConfiguration {

    @Resource
    private Environment env;

    private EventRedisProperties eventRedisProperties;

    public EventRedisAutoConfiguration(EventRedisProperties eventRedisProperties) {
        this.eventRedisProperties = eventRedisProperties;
    }

    @Bean
    public CustomConfigurationPropertiesBinder CustomConfigurationPropertiesBinder(ApplicationContext applicationContext,
                                                                                   ConversionService conversionService) {
        return new CustomConfigurationPropertiesBinder(applicationContext, conversionService);
    }


    @Bean
    @ConditionalOnMissingBean(RedisEventBus.class)
    public EventBus redisEventBus(SubscriberRegistry subscriberRegistry, CustomConfigurationPropertiesBinder customConfigurationPropertiesBinder) {
        if (eventRedisProperties.isPrefixWithSpring()) {
            customConfigurationPropertiesBinder.bind(eventRedisProperties, "eventRedisProperties", "spring.redis");
        }
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        if (eventRedisProperties.getJedis() != null) {
            RedisProperties.Pool pool = eventRedisProperties.getJedis().getPool();
            if (pool != null) {
                poolConfig.setMaxIdle(pool.getMaxIdle());
                poolConfig.setMaxTotal(pool.getMaxActive());
                poolConfig.setMinIdle(pool.getMinIdle());
                poolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
            }
        }

        Pool<Jedis> jedisPool = null;
        if (eventRedisProperties.getSentinel() != null) {
            RedisProperties.Sentinel sentinel = eventRedisProperties.getSentinel();
            jedisPool = new JedisSentinelPool(sentinel.getMaster(),
                    new HashSet<>(sentinel.getNodes()),
                    poolConfig,
                    (int) eventRedisProperties.getTimeout().toMillis(),
                    eventRedisProperties.getPassword(),
                    eventRedisProperties.getDatabase()
            );
        } else if (eventRedisProperties.getCluster() != null) {
            RedisProperties.Cluster cluster = eventRedisProperties.getCluster();

            Set<HostAndPort> nodes = new HashSet<>();
            for (String node : cluster.getNodes()) {
                nodes.add(HostAndPort.parseString(node));
            }
            JedisCluster jedisCluster = new JedisCluster(nodes,
                    (int) eventRedisProperties.getTimeout().toMillis(),
                    cluster.getMaxRedirects(),
                    poolConfig);
            //todo:jedisCluster to pool
        } else if (StringUtils.hasText(eventRedisProperties.getUrl())) {
            HostAndPort hostAndPort = HostAndPort.parseString(eventRedisProperties.getUrl());
            jedisPool = new JedisPool(poolConfig,
                    hostAndPort.getHost(),
                    hostAndPort.getPort(),
                    (int) eventRedisProperties.getTimeout().toMillis(),
                    eventRedisProperties.getPassword(),
                    eventRedisProperties.getDatabase());
        } else {
            jedisPool = new JedisPool(poolConfig,
                    eventRedisProperties.getHost(),
                    eventRedisProperties.getPort(),
                    (int) eventRedisProperties.getTimeout().toMillis(),
                    eventRedisProperties.getPassword(),
                    eventRedisProperties.getDatabase());
        }

        if (eventRedisProperties.getEventThread() == -1) {
            return new RedisEventBus(subscriberRegistry, new ExecutorDispatcher(), jedisPool);
        } else {
            return new RedisEventBus(subscriberRegistry,
                    new ExecutorDispatcher(Executors.newFixedThreadPool(eventRedisProperties.getEventThread())),
                    jedisPool);
        }
    }
}

