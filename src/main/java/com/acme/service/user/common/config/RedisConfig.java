/*----------------------------------------------------------------------------*/
/* Source File:   REDISCONFIG.JAVA                                            */
/* Copyright (c), 2025 Acme                                                   */
/*----------------------------------------------------------------------------*/
/*-----------------------------------------------------------------------------
 History
 Feb.13/2025  COQ  File created.
 -----------------------------------------------------------------------------*/
package com.acme.service.user.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configure Redis Template type. Used in communicating with Redis Server.
 *
 * @author COQ - Carlos Adolfo Ortiz Q.
 */
@Configuration
public class RedisConfig {

    /**
     * Registers a bean to handle all Redis operations.
     *
     * @param redisConnectionFactory A reference bean indicating the definition to the Redis Connection Factory representation.
     * @return A reference bean to a {@link RedisTemplate}
     */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        var redisTemplate = new RedisTemplate<String, Object>();
        var stringRedisSerializer = new StringRedisSerializer();
        var serializer = new GenericJackson2JsonRedisSerializer();

        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setEnableTransactionSupport(true);

        return redisTemplate;
    }
}
