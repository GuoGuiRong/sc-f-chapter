package com.ggr.serviceredis.service.impl;

import com.ggr.serviceredis.config.redis.JedisCallable;
import com.ggr.serviceredis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Optional;

/**
 * Redis基础服务组件
 */
@Component
@Slf4j
public class RedisServiceImpl implements RedisService {

    @Autowired
    JedisPool jedisPool;

    @Override
    public <T> Optional<T> doInJedis(JedisCallable<T> jedisCallable){
        T result = null;
        try(Jedis instance = jedisPool.getResource()) {
            result = jedisCallable.call(instance);
            log.info("redis执行返回结果result={}",result);
        }catch (Exception e){
            log.error("redis出错了e={}",e);
        }
        return Optional.ofNullable(result);
    }
}
