package com.ggr.serviceredis.service;

public interface RedisLockService {

    /**
     * 尝试CAS方式获取锁
     * @param key 锁的键
     * @param value 锁的值
     * @param expire 过期时间 单位 EX = seconds; PX = milliseconds
     * @param timeout 尝试获取锁超时时间 单位毫秒
     * @return
     */
    boolean tryLock(String key, String value, long expire, long timeout);

    /**
     * 释放锁
     * @param key  锁的键
     * @param value 锁的值，为了避免锁被别人释放，需要这个所得值
     * @return
     */
    boolean unLock(String key, String value);
}
