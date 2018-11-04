package com.ggr.serviceredis.service.impl;

import com.ggr.serviceredis.service.RateLimiterService;
import com.ggr.serviceredis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.io.IOException;
import java.util.Optional;

/**
 * Redis 限流器简单实现
 */
@Component
@Slf4j
public class SimpleRateLimiterServiceImpl implements RateLimiterService {

    @Autowired
    RedisService redisService;

    @Override
    public boolean isActionAllowed(String userId, String actionKey, int period, int maxCount) {
        String key = String.format("hist:%s:%s", userId, actionKey);
        long nowTs = System.currentTimeMillis();
        Optional<Boolean> optionalBoolean = redisService.doInJedis(jedis -> {
            try {
                Pipeline pipe = jedis.pipelined();
                pipe.multi();
                pipe.zadd(key, nowTs, "" + nowTs);
                pipe.zremrangeByScore(key, 0, nowTs - period * 1000);
                Response<Long> count = pipe.zcard(key);
                pipe.expire(key, period + 1);
                pipe.exec();
                pipe.close();
                return count.get() <= maxCount;
            } catch (IOException e) {
                log.error("Redis出错了e={}",e);
            }
            return false;
        });
        return optionalBoolean.orElse(false);
    }
}
