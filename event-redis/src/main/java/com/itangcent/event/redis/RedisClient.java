package com.itangcent.event.redis;

import redis.clients.jedis.BinaryJedisPubSub;

public interface RedisClient {

    public void subscribe(BinaryJedisPubSub jedisPubSub, byte[]... channels);

    public Long publish(byte[] channel, byte[] message);
}
