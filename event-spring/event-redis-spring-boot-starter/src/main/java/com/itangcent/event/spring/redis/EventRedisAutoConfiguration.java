package com.itangcent.event.spring.redis;

import com.itangcent.event.EventBus;
import com.itangcent.event.ExecutorDispatcher;
import com.itangcent.event.SubscriberRegistry;
import com.itangcent.event.redis.JedisPoolClient;
import com.itangcent.event.redis.RedisClient;
import com.itangcent.event.redis.RedisEventBus;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;

@EnableConfigurationProperties({EventRedisProperties.class})
public class EventRedisAutoConfiguration {

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

        RedisClient redisClient = null;
        if (eventRedisProperties.getSentinel() != null) {
            RedisProperties.Sentinel sentinel = eventRedisProperties.getSentinel();
            redisClient = new JedisPoolClient(new JedisSentinelPool(sentinel.getMaster(),
                    new HashSet<>(sentinel.getNodes()),
                    poolConfig,
                    (int) eventRedisProperties.getTimeout().toMillis(),
                    eventRedisProperties.getPassword(),
                    eventRedisProperties.getDatabase()
            ));
        } else if (eventRedisProperties.getCluster() != null) {
            RedisProperties.Cluster cluster = eventRedisProperties.getCluster();

            Set<HostAndPort> nodes = new HashSet<>();
            for (String node : cluster.getNodes()) {
                nodes.add(HostAndPort.parseString(node));
            }
            redisClient = new JedisClusterClient(new JedisCluster(nodes,
                    (int) eventRedisProperties.getTimeout().toMillis(),
                    cluster.getMaxRedirects(),
                    poolConfig));
        } else if (StringUtils.hasText(eventRedisProperties.getUrl())) {
            HostAndPort hostAndPort = HostAndPort.parseString(eventRedisProperties.getUrl());
            redisClient = new JedisPoolClient(new JedisPool(poolConfig,
                    hostAndPort.getHost(),
                    hostAndPort.getPort(),
                    (int) eventRedisProperties.getTimeout().toMillis(),
                    eventRedisProperties.getPassword(),
                    eventRedisProperties.getDatabase()));
        } else {
            redisClient = new JedisPoolClient(new JedisPool(poolConfig,
                    eventRedisProperties.getHost(),
                    eventRedisProperties.getPort(),
                    (int) eventRedisProperties.getTimeout().toMillis(),
                    eventRedisProperties.getPassword(),
                    eventRedisProperties.getDatabase()));
        }

        if (eventRedisProperties.getEventThread() == -1) {
            return new RedisEventBus(subscriberRegistry, new ExecutorDispatcher(), redisClient);
        } else {
            return new RedisEventBus(subscriberRegistry,
                    new ExecutorDispatcher(eventRedisProperties.getEventThread()),
                    redisClient);
        }
    }
}

