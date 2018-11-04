package com.ggr.serviceredis.service;

import com.ggr.serviceredis.config.redis.JedisCallable;
import java.util.Optional;

/**
 * Redis基础服务组件
 */
public interface RedisService {

    /**
     * 通过资源池的方式提交
     * @param jedisCallable 与提交的task
     * @param <T> 返回值类型
     * @return
     */
    <T> Optional<T> doInJedis(JedisCallable<T> jedisCallable);

}
