package com.ggr.serviceredis.config.redis;

import redis.clients.jedis.Jedis;

public interface JedisCallable<T> {
    T call(Jedis jedis);
}
