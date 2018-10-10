package com.itangcent.event.spring.redis;

import com.itangcent.event.redis.RedisClient;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.JedisCluster;

public class JedisClusterClient implements RedisClient {
    private JedisCluster jedisCluster;

    public JedisClusterClient(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    @Override
    public void subscribe(BinaryJedisPubSub jedisPubSub, byte[]... channels) {
        jedisCluster.subscribe(jedisPubSub, channels);
    }

    @Override
    public Long publish(byte[] channel, byte[] message) {
        return jedisCluster.publish(channel, message);
    }
}
