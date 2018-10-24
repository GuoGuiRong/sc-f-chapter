package com.ggr.serviceredis.service.impl;

import com.ggr.serviceredis.service.RedisLockService;
import com.ggr.serviceredis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

/**
 * Redis 锁的提供方
 * redis基本原理：
 * 1. 非堵塞方式获取锁，使用CAS提高获取锁的成功率
 * 2. 保证加锁和释放锁的是同一个人
 * 3. 必须设置过期时间避免死锁
 */
@Component
@Slf4j
public class RedisLockServiceImpl implements RedisLockService {

    @Autowired
    RedisService redisService;
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;

    @Override
    public boolean tryLock(String key, String value, long expire, long timeout) {

        Optional<Boolean> optional = redisService.doInJedis(jedis -> {
            long startTime = System.currentTimeMillis();
            try {
                do {
                    String result = jedis.set(key, value, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expire);
                    if (LOCK_SUCCESS.equals(result)) {
                        log.info("redis加锁成功key={},value={},expire={}",key,value,expire);
                        return true;
                    }
                    //当超时时间设置的比较的的时候，可以做竞争锁的线程可以适当的休眠一会儿
                    if (timeout - System.currentTimeMillis() > 1000) {
                        Thread.sleep(500);
                    }
                } while (System.currentTimeMillis() - startTime > timeout);
            } catch (InterruptedException e) {
                log.error("redis加锁时出错了e={}",e);
            }
            log.info("redis加锁失败key={},value={},expire={}",key,value,expire);
            return false;
        });
        return optional.orElse(false);
    }

    @Override
    public boolean unLock(String key, String value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Optional<Boolean> optional = redisService.doInJedis(jedis -> {
            Object result = jedis.eval(script, Collections.singletonList(key), Collections.singletonList(value));
            if (RELEASE_SUCCESS.equals(result)) {
                return true;
            }
            return false;
        });
        return optional.orElse(false);
    }
}
