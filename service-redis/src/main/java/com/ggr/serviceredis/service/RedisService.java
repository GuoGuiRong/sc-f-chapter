package com.ggr.serviceredis.service;

import com.ggr.serviceredis.config.redis.JedisCallable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Optional;


@Component
@Slf4j
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    /**
     * 通过资源池的方式提交
     * @param jedisCallable 与提交的task
     * @param <T> 返回值类型
     * @return
     */
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
