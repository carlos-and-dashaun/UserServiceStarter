/*----------------------------------------------------------------------------*/
/* Source File:   ABSTRACTREDISCACHESERVICE.JAVA                              */
/* Copyright (c), 2025 Acme                                                   */
/*----------------------------------------------------------------------------*/
/*-----------------------------------------------------------------------------
 History
 Feb.13/2025  COQ  File created.
 -----------------------------------------------------------------------------*/
package com.acme.service.user.service;

import static com.acme.service.user.common.consts.GlobalConstants.DOT;
import static com.acme.service.user.common.consts.GlobalConstants.INT_ONE;
import static com.acme.service.user.common.consts.GlobalConstants.INT_ZERO;
import static com.acme.service.user.common.consts.GlobalConstants.WILD_CARD_ASTERISK;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Includes helpers for Cache Service implementations.
 *
 * @param <K> Indicates the Redis key type (usually a String) to work with.
 * @param <V> Indicates the Redis Value type to work with.
 * @author COQ - Carlos Adolfo Ortiz Q.
 */
public abstract class AbstractBaseRedisCacheService<K, V> {
    protected final String cacheName;
    protected final RedisTemplate<K, V> redisTemplate;

    /**
     * Constructor with parameters.
     *
     * @param cacheName     Represents the 'Cache Region' name. It is used as part of a key such as @{code myregion.item}.
     * @param redisTemplate References the abstraction to communicate to a Redis server.
     * @see RedisTemplate
     */
    public AbstractBaseRedisCacheService(String cacheName, RedisTemplate<K, V> redisTemplate) {
        this.cacheName = cacheName;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Creates a joined map from the items stored in each list supplied. It is used in {@code Cache Region}
     * implementations.
     *
     * @param keys   Contains a list of indices (Strings) representing the type of data in the cache.
     * @param values Contains a list of types stored in the cache associated to a {@code key}.
     * @return A map containing the combination of {@code keys} and {@code values}.
     */
    protected HashMap<K, V> fillCacheMapRegionUsing(List<K> keys, List<V> values) {
        // Keys must have the 'Cache Region' removed.
        var keysWithNoRegion = removeCacheRegion(keys);

        return fillCacheMapUsing(keysWithNoRegion, values);
    }

    /**
     * Creates a joined map from the items stored in each list supplied. It is used in {@code Cache Hash}
     * implementations.
     * <br/>
     * <b>NOTE:</b>If a value for a key is not found, then it is filtered out.
     *
     * @param keys   Contains a list of indices (Strings) representing the type of data in the cache.
     * @param values Contains a list of types stored in the cache associated with a {@code key}.
     * @return A concrete map type with the combined data.
     */
    protected HashMap<K, V> fillCacheMapUsing(List<K> keys, List<V> values) {
        var map = new LinkedHashMap<K, V>();

        IntStream.range(INT_ZERO, keys.size())
            .forEach(keyPos -> {
                var key = keys.get(keyPos);
                var value = values.get(keyPos);

                if (value != null) {
                    map.put(key, value);
                }
            });

        return map;
    }

    /**
     * Removes the {@code Cache Region or Cache Name} contained in the supplied list of keys.
     *
     * @param keys Contains a list of indices (Strings) representing the type of data in the cache.
     * @return A list without the {@code Cache Region}.
     */
    protected List<K> removeCacheRegion(List<K> keys) {
        return
            keys.stream()
                .map(this::removeCacheRegionFrom)
                .toList();
    }

    /**
     * Converts a list from the {@code Collection} contract to the {@code List} to comply with API definitions.
     *
     * @param keys Contains a list of indices (Strings) representing the type of data in the cache.
     * @return The converted List of keys.
     */
    protected List<K> transformKeyForRegionInList(Collection<K> keys) {
        return
            keys.stream()
                .map(this::buildRegionKey)
                .toList();
    }

    /**
     * Transforms a map which contains the {@code Cache Region} as part of the key to a map without this.
     *
     * @param map Contains the information to be processed.
     * @return A new map without the {@code Cache Region} as part of the key
     */
    protected Map<K, V> transformKeyForRegionInMap(Map<K, V> map) {
        var newMap = new LinkedHashMap<K, V>();

        map.forEach((k, v) -> newMap.put(buildRegionKey(k), v));

        return newMap;
    }

    /**
     * Processes the {@code key} supplied by removing the {@code Cache Region} it holds.
     *
     * @param key Contains the index (String) representing the type of data in the cache.
     * @return A new &lt;K&gt; without the {@code Cache Region}.
     */
    protected K removeCacheRegionFrom(K key) {
        // If key already contains the 'Cache Region' it does nothing.
        if (key instanceof String keyStr) {
            if (keyStr.contains(cacheName)) {
                return (K) keyStr.substring(cacheName.length() + INT_ONE);
            }
        }

        return key;
    }

    /**
     * Given the {@code key} it prefixes the {@code Cache Region}. Example, if {@code key} is {@code item} and the
     * {@code Cache Region} is {@code myregion} then the result is {@code myregion.item}.
     *
     * @param key Contains the index (String) representing the type of data in the cache.
     * @return A modified key in the format {@code [Cache Region].[key]}, e.g., {@code myregion.item}.
     */
    protected K buildRegionKey(K key) {
        // If key already contains the 'Cache Name' it does nothing.
        if (key instanceof String keyStr) {
            if (!keyStr.contains(cacheName)) {
                return (K) ((cacheName + DOT) + key);
            }
        }

        return key;
    }

    /**
     * Verifies that the supplied {@code keyPattern} when set to null then it gets modified to an empty string and if
     * the wild card usd to filter Redis keys is not present then it appends it.
     *
     * @param keyPattern Denotes the expression to use for filtering the keys to be matched, usually a wild card
     *                   (an asterisk) is added. This must not be {@code NULL}. Example criteria could be {@code *12*}
     *                   which looks for keys that contains any characters then has a '12' in the middle and ends with
     *                   any characters. Here, if you pass an empty string, the system will interpret this as if you
     *                   want to count all items, otherwise the pattern limits the items being considered. Take into
     *                   account that this is case-sensitive.
     * @return A new key pattern to work with the Redis Server.
     */
    protected String verifyKeyPattern(String keyPattern) {
        if (null == keyPattern) {
            keyPattern = StringUtils.EMPTY;
        }

        if (!keyPattern.contains(WILD_CARD_ASTERISK)) {
            keyPattern += WILD_CARD_ASTERISK;
        }

        return keyPattern;
    }

    /**
     * Builds a {code key} in the format {@code [Cache Region].[key]}, e.g., {@code myregion.item}.
     *
     * @param keyPattern Denotes the expression to use for filtering the keys to be matched, usually a wild card
     *                   (an asterisk) is added. This must not be {@code NULL}. Example criteria could be {@code *12*}
     *                   which looks for keys that contains any characters then has a '12' in the middle and ends with
     *                   any characters. Here, if you pass an empty string, the system will interpret this as if you
     *                   want to count all items, otherwise the pattern limits the items being considered. Take into
     *                   account that this is case-sensitive.
     * @return A new key with prefixed with the {@code Cache Region}.
     */
    protected K buildRegionKeyPattern(String keyPattern) {
        return (K) (cacheName + DOT + verifyKeyPattern(keyPattern));
    }
}
