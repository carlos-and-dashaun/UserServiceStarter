/*----------------------------------------------------------------------------*/
/* Source File:   REDISREGIONCACHESERVICE.JAVA                                */
/* Copyright (c), 2025 Acme                                                   */
/*----------------------------------------------------------------------------*/
/*-----------------------------------------------------------------------------
 History
 Feb.13/2025  COQ  File created.
 -----------------------------------------------------------------------------*/
package com.acme.service.user.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Implements a Region Redis Cache Service. All the &lt;K&gt; beans are assumed to be of {@code String} type, but
 * they are manipulated in order to include the {@code cacheRegion} prefix to each usage of them to actually refer to
 * all the Key/Value pairs stored in the Redis Store.
 *
 * @param <K> Indicates the Redis key type (usually a String) to work with.
 * @param <V> Indicates the Redis Value type to work with.
 * @author COQ - Carlos Adolfo Ortiz Q.
 * @see AbstractBaseRedisCacheService
 * @see RedisCacheService
 */
@Service
public class RedisRegionCacheService<K, V> extends AbstractBaseRedisCacheService<K, V> implements RedisCacheService<K, V> {
    /**
     * Constructor with parameters.
     *
     * @param cacheName     Represents the 'Cache Region' name. It is used as part of a key such as {@code myregion.item}.
     * @param redisTemplate References the abstraction to communicate to a Redis server.
     * @see RedisTemplate
     */
    public RedisRegionCacheService(String cacheName, RedisTemplate<K, V> redisTemplate) {
        super(cacheName, redisTemplate);
    }

    @Override
    public Boolean exists(K key) {
        return redisTemplate.hasKey(buildRegionKey(key));
    }

    @Override
    public Long count() {
        return count(StringUtils.EMPTY);
    }

    @Override
    public Long count(String keyPattern) {
        return (long) CollectionUtils.emptyIfNull(redisTemplate.keys(buildRegionKeyPattern(keyPattern))).size();
    }

    @Override
    public void insert(K key, V info) {
        redisTemplate.opsForValue().set(buildRegionKey(key), info);
    }

    @Override
    public void multiInsert(Map<K, V> map) {
        redisTemplate.opsForValue().multiSet(transformKeyForRegionInMap(map));
    }

    @Override
    public V retrieve(K key) {
        return redisTemplate.opsForValue().get(buildRegionKey(key));
    }

    @Override
    public List<K> multiRetrieveKeyList(String keyPattern) {
        return
            removeCacheRegion(
                CollectionUtils.emptyIfNull(redisTemplate.keys(buildRegionKeyPattern(keyPattern)))
                    .stream()
                    .toList()
            );
    }

    @Override
    public List<V> multiRetrieveList(String keyPattern) {
        return
            redisTemplate
                .opsForValue()
                .multiGet(CollectionUtils.emptyIfNull(redisTemplate.keys(buildRegionKeyPattern(keyPattern))));
    }

    @Override
    public List<V> multiRetrieveList(Collection<K> keys) {
        return
            redisTemplate
                .opsForValue()
                .multiGet(transformKeyForRegionInList(keys));
    }

    @Override
    public Map<K, V> multiRetrieveMap() {
        return multiRetrieveMap(StringUtils.EMPTY);
    }

    @Override
    public Map<K, V> multiRetrieveMap(String keyPattern) {
        var keys =
            CollectionUtils.emptyIfNull(redisTemplate.keys(buildRegionKeyPattern(keyPattern)))
                .stream()
                .toList();
        var values = redisTemplate.opsForValue().multiGet(keys);

        return fillCacheMapRegionUsing(keys, values);
    }

    @Override
    public Map<K, V> multiRetrieveMap(Collection<K> keys) {
        var keysInRegion = transformKeyForRegionInList(keys);
        var values = multiRetrieveList(keys);

        return fillCacheMapRegionUsing(keysInRegion, values);
    }

    @Override
    public Boolean delete(K key) {
        return redisTemplate.delete(buildRegionKey(key));
    }

    @Override
    public Long delete(Collection<K> keys) {
        return redisTemplate.delete(transformKeyForRegionInList(keys));
    }
}
