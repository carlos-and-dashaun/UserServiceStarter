/*----------------------------------------------------------------------------*/
/* Source File:   REDISHASHCACHESERVICE.JAVA                                  */
/* Copyright (c), 2025 Acme                                                   */
/*----------------------------------------------------------------------------*/
/*-----------------------------------------------------------------------------
 History
 Feb.13/2025  COQ  File created.
 -----------------------------------------------------------------------------*/
package com.acme.service.user.service;

import static com.acme.service.user.common.consts.GlobalConstants.INT_ZERO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

/**
 * Implements a Hash Redis Cache Service. All the &lt;K&gt; beans are assumed to be of {@code String} type.
 *
 * @param <K> Indicates the Redis key type (usually a String) to work with.
 * @param <V> Indicates the Redis Value type to work with.
 * @author COQ - Carlos Adolfo Ortiz Q.
 * @see AbstractBaseRedisCacheService
 * @see RedisCacheService
 */
@Service
public class RedisHashCacheService<K, V> extends AbstractBaseRedisCacheService<K, V> implements RedisCacheService<K, V> {
    /**
     * Constructor with Parameters.
     *
     * @param cacheName     Represents the 'Cache Hash' name. It is used as part of a key such as @{code item}.
     * @param redisTemplate redisTemplate References the abstraction to communicate to a Redis server.
     */
    public RedisHashCacheService(String cacheName, RedisTemplate<K, V> redisTemplate) {
        super(cacheName, redisTemplate);
    }

    @Override
    public Boolean exists(K key) {
        return redisTemplate.opsForHash().hasKey((K) cacheName, key);
    }

    public Long count() {
        return redisTemplate.opsForHash().size((K) cacheName);
    }

    @Override
    public Long count(String keyPattern) {
        return Long.valueOf(multiRetrieveList(keyPattern).size());
    }

    @Override
    public void insert(K key, V info) {
        redisTemplate.opsForHash().put((K) cacheName, key, info);
    }

    @Override
    public void multiInsert(Map<K, V> map) {
        redisTemplate.opsForHash().putAll((K) cacheName, map);
    }

    @Override
    public V retrieve(K key) {
        return (V) redisTemplate.opsForHash().get((K) cacheName, key);
    }

    @Override
    public List<K> multiRetrieveKeyList(String keyPattern) {
        var hOps = redisTemplate.opsForHash();
        var scanOptions = ScanOptions.scanOptions().match(verifyKeyPattern(keyPattern)).build();

        try (var mapCursor = hOps.scan((K) cacheName, scanOptions)) {
            return
                mapCursor
                    .stream()
                    .map(cursorItem -> ((Map.Entry<K, V>) cursorItem).getKey())
                    .toList();
        }
    }

    @Override
    public List<V> multiRetrieveList(String keyPattern) {
        var hOps = redisTemplate.opsForHash();
        var scanOptions = ScanOptions.scanOptions().match(verifyKeyPattern(keyPattern)).build();

        try (var mapCursor = hOps.scan((K) cacheName, scanOptions)) {
            return
                mapCursor
                    .stream()
                    .map(cursorItem -> ((Map.Entry<K, V>) cursorItem).getValue())
                    .toList();
        }
    }

    @Override
    public List<V> multiRetrieveList(Collection<K> keys) {
        return redisTemplate
            .opsForHash()
            .multiGet(
                (K) cacheName,
                keys.stream()
                    .map(key -> (Object) key)
                    .toList()
            )
            .stream()
            .map(value -> (V) value)
            .toList();
    }

    @Override
    public Map<K, V> multiRetrieveMap() {
        return (Map<K, V>) redisTemplate.opsForHash().entries((K) cacheName);
    }

    @Override
    public Map<K, V> multiRetrieveMap(String keyPattern) {
        var hOps = redisTemplate.opsForHash();
        var scanOptions = ScanOptions.scanOptions().match(verifyKeyPattern(keyPattern)).build();

        try (var mapCursor = hOps.scan((K) cacheName, scanOptions)) {
            return
                mapCursor
                    .stream()
                    .map(cursorItem -> (Map.Entry<K, V>) cursorItem)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }

    @Override
    public Map<K, V> multiRetrieveMap(Collection<K> keys) {
        return
            fillCacheMapUsing(
                new ArrayList<>(keys),
                multiRetrieveList(keys)
            );
    }

    @Override
    public Boolean delete(K key) {
        return redisTemplate.opsForHash().delete((K) cacheName, key) != INT_ZERO;
    }

    @Override
    public Long delete(Collection<K> keys) {
        return redisTemplate.opsForHash().delete((K) cacheName, keys.toArray());
    }
}
