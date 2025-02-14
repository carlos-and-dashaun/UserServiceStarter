/*----------------------------------------------------------------------------*/
/* Source File:   REDISCACHECONFIG.JAVA                                       */
/* Copyright (c), 2025 Acme                                                   */
/*----------------------------------------------------------------------------*/
/*-----------------------------------------------------------------------------
 History
 Feb.13/2025  COQ  File created.
 -----------------------------------------------------------------------------*/
package com.acme.service.user.common.config;

import static com.acme.service.user.common.consts.GlobalConstants.USER_RECORD_CACHE_KEY;

import com.acme.service.user.service.RedisCacheService;
import com.acme.service.user.service.RedisHashCacheService;
import com.acme.service.user.service.RedisRegionCacheService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Defines the beans necessary to activate {@link RedisCacheService} implementation
 * by using a profile.
 *
 * @author COQ - Carlos Adolfo Ortiz Q.
 */
@Configuration
public class RedisCacheConfig {

    @Profile("redis-region")
    @Bean("redisUserRecordCacheService")
    public RedisCacheService<String, Object> redisRegionCacheService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisRegionCacheService<>(USER_RECORD_CACHE_KEY, redisTemplate);
    }

    @Profile("redis-hash")
    @Bean("redisUserRecordCacheService")
    public RedisCacheService<String, Object> redisHashCacheService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisHashCacheService<>(USER_RECORD_CACHE_KEY, redisTemplate);
    }
}
