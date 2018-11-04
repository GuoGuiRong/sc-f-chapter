package com.ggr.serviceredis.service;

/**
 * 简单的限流器
 */
public interface RateLimiterService {

    /**
     * 在过期时间内允许请求maxCount次
     * @param userId 请求标志
     * @param actionKey 用户行为标志
     * @param period 过期时间
     * @param maxCount 请求次数
     * @return
     */
    boolean isActionAllowed(String userId, String actionKey, int period, int maxCount);
}
