/*----------------------------------------------------------------------------*/
/* Source File:   REDISCACHESERVICE.JAVA                                      */
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

/**
 * Defines the contract necessary for making Redis server operations. Eventually this will be a wrapper around
 * the good {@code redisTemplate} abstraction.
 *
 * @param <K> Indicates the Redis key type (usually a String) to work with.
 * @param <V> Indicates the Redis Value type to work with.
 * @author COQ - Carlos Adolfo Ortiz Q.
 * @see org.springframework.data.redis.core.RedisTemplate
 */
public interface RedisCacheService<K, V> {
    /**
     * Determines the presence of the 'key' in the database.
     *
     * @param key Indicates the Redis key type (usually a String) to work with, it must be not null.
     * @return A {@link Boolean} true if it is there.
     */
    Boolean exists(K key);

    /**
     * Determines the number of items stored in the cache.
     *
     * @return The number of items stored in the cache.
     */
    Long count();

    /**
     * Determines the number of items stored in the cache using the {@code keyPattern} to filter items to consider.
     *
     * @param keyPattern Denotes the expression to use for filtering the keys to be matched, usually a wild card
     *                   (a asterisk) is added. This must not be {@code NULL}. Example criteria could be {@code *12*}
     *                   which looks for keys that contains any characters then has a '12' in the middle and ends with
     *                   any characters. Here, if you pass an empty string, the system will interpret this as if you
     *                   want to count all items, otherwise the pattern limits the items being considered. Take into
     *                   account that this is case-sensitive.
     * @return The number of items stored in the cache.
     */
    Long count(String keyPattern);

    /**
     * Performs the addition of the pair (key, value) into the Redis store.
     *
     * @param key  Indicates the Redis key type (usually a String) to work with, it must be not null.
     * @param info Indicates the data to be stored, it must be not null.
     */
    void insert(K key, V info);

    /**
     * Performs the addition of a bulk of pairs (key, value) into the Redis store. Keep in mind tha the keys must
     * be valid values according to the implementer details. For instance, if the implemetation detail is to use
     * 'region key' then each key must have it prefixed the region, somehow the 'map' may not come with those
     * prefixed as this is the responsibility of the implementer.
     *
     * @param map Contains the (key, value) pairs to be added to the Redis store.
     */
    void multiInsert(Map<K, V> map);

    /**
     * Looks for the given 'key' in the Redis Store.
     *
     * @param key Indicates the Redis key type (usually a String) to work with, it must be not null.
     * @return The bean present in the Redis Store or {@code NULL} if not there.
     */
    V retrieve(K key);

    /**
     * Look for the keys stored in the Redis Store that matches the {@code keyPattern}. Implementers can manipulate
     * the returned set to fit their needs.
     *
     * @param keyPattern Denotes the expression to use for filtering the keys to be matched, usually a wild card
     *                   (an asterisk) is added. This must not be {@code NULL}. Example criteria could be {@code *12*}
     *                   which looks for keys that contains any characters then has a '12' in the middle and ends with
     *                   any characters
     * @return A list of &lt;K&gt; beans present in the Redis Store.
     */
    List<K> multiRetrieveKeyList(String keyPattern);

    /**
     * Find all {@code keys} matching the given {@code keyPattern}. The implementer can manipulate the given
     * {@code keyPattern} to suit its needs.
     *
     * @param keyPattern Denotes the expression to use for filtering the keys to be matched, usually a wild card
     *                   (an asterisk) is added. This must not be {@code NULL}. Example criteria could be {@code *12*}
     *                   which looks for keys that contains any characters then has a '12' in the middle and ends with
     *                   any characters. Here, if you pass an empty string, the system will interpret this as if you
     *                   want to count all items, otherwise the pattern limits the items being considered. Take into
     *                   account that this is case-sensitive.
     * @return A list of &lt;V&gt; beans present in the Redis Store.
     */
    List<V> multiRetrieveList(String keyPattern);

    /**
     * Find all {@code key/value} in the Redis Store according to the list of {@code keys}. Implementers are
     * responsible for manipulating these {@code keys} to suit their needs.
     *
     * @param keys Contains a list of &lt;K&gt; beans to extract from the Redis Store. Normally the &lt;K&gt; type is
     *             a {@code String}.
     * @return A list of &lt;V&gt; beans present in the Redis Store.
     */
    List<V> multiRetrieveList(Collection<K> keys);

    /**
     * Retrieves all the {@code keys} stored in the cache.
     *
     * @return A Map of &lt;K, V&gt; beans present in the Redis Store. Implementer can manipulate the keys.
     */
    Map<K, V> multiRetrieveMap();

    /**
     * Find all {@code key/value} matching the given {@code keyPattern}. The implementer can manipulate
     * the given {@code keyPattern} to suit its needs.
     *
     * @param keyPattern Denotes the expression to use for filtering the keys to be matched, usually a wild card
     *                   (an asterisk) is added. This must not be {@code NULL}. Example criteria could be {@code *12*}
     *                   which looks for keys that contains any characters then has a '12' in the middle and ends with
     *                   any characters. Here, if you pass an empty string, the system will interpret this as if you
     *                   want to count all items, otherwise the pattern limits the items being considered. Take into
     *                   account that this is case-sensitive.
     * @return A Map of &lt;K, V&gt; beans present in the Redis Store. Implementer can manipulate the keys.
     */
    Map<K, V> multiRetrieveMap(String keyPattern);

    /**
     * Find all {@code key/value} data matching the given {@code keys}. The implementer can manipulate the given
     * {@code keys} to suit its needs.
     * <br/>
     * <b>NOTE:</b> If a key is not found then it must not be included in the response map, that is, it is considered
     * as not found if it is not present in the map.
     *
     * @param keys Contains a list of &lt;K&gt; beans to extract from the Redis Store. Normally the &lt;K&gt; type is
     *             a {@code String}.
     * @return A Map of &lt;K, V&gt; beans present in the Redis Store. Implementer can manipulate the keys.
     */
    Map<K, V> multiRetrieveMap(Collection<K> keys);

    /**
     * Removes the key from the store.
     *
     * @param key Indicates the Redis key type (usually a String) to work with, it must be not null.
     * @return A {@link Boolean} true if removed.
     */
    Boolean delete(K key);

    /**
     * Removes all the matching {@code keys}. The implementer can manipulate the given {@code keys} to suit its needs.
     *
     * @param keys Contains a list of &lt;K&gt; beans to extract from the Redis Store. Normally the &lt;K&gt; type is
     *             a {@code String}.
     * @return The number of {@code keys} removed from the Redis Store.
     */
    Long delete(Collection<K> keys);
}
