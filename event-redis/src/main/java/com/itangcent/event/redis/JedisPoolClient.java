package com.itangcent.event.redis;

import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

public class JedisPoolClient implements RedisClient {
    private Pool<Jedis> jedisPool;

    public JedisPoolClient(Pool<Jedis> jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public void subscribe(BinaryJedisPubSub jedisPubSub, byte[]... channels) {
        try (Jedis resource = jedisPool.getResource()) {
            resource.subscribe(jedisPubSub, channels);
        }
    }

    @Override
    public Long publish(byte[] channel, byte[] message) {
        try (Jedis resource = jedisPool.getResource()) {
            return resource.publish(channel, message);
        }
    }
}
