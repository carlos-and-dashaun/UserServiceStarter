/*----------------------------------------------------------------------------*/
/* Source File:   REDISHASHCACHESERVICETEST.JAVA                              */
/* Copyright (c), 2025 Acme                                                   */
/*----------------------------------------------------------------------------*/
/*-----------------------------------------------------------------------------
 History
 Feb.17/2025  COQ  File created.
 -----------------------------------------------------------------------------*/
package com.acme.service.user.service;

import static com.acme.service.user.common.consts.GlobalConstants.INT_THREE;
import static com.acme.service.user.common.consts.GlobalConstants.INT_TWO;
import static com.acme.service.user.common.consts.GlobalConstants.INT_ZERO;
import static com.acme.service.user.common.consts.GlobalConstants.LONG_ONE;
import static com.acme.service.user.common.consts.GlobalConstants.LONG_THREE;
import static com.acme.service.user.common.consts.GlobalConstants.LONG_ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanCursor;
import org.springframework.data.redis.core.ScanIteration;
import org.springframework.data.redis.core.ScanOptions;

/**
 * Unit Tests for {@link RedisHashCacheService}.
 *
 * @author COQ - Carlos Adolfo Ortiz Q.
 */
@ExtendWith(MockitoExtension.class)
class RedisHashCacheServiceTest {
    private static final String REDIS_HASH = "hash";
    private static final String REDIS_THE_KEY = "thekey";
    private static final String REDIS_THE_INFO_SAVED = "The Info saved";
    private static final String REDIS_KEY_ITEM_1234 = "ITEM1234";
    private static final String REDIS_KEY_ITEM_4567 = "ITEM4567";
    private static final String REDIS_KEY_ITEM_8901 = "ITEM8901";
    private static final String REDIS_VALUE_ITEM_1234 = "VALUE 1234";
    private static final String REDIS_VALUE_ITEM_4567 = "VALUE 4567";
    private static final String REDIS_VALUE_ITEM_8901 = "VALUE 8901";
    private static final String KEY_PATTERN_AA = "AA*";

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;
    private RedisHashCacheService<String, String> redisHashCacheService;

    @BeforeEach
    void beforeEach() {
        redisHashCacheService = new RedisHashCacheService<>(REDIS_HASH, redisTemplate);
        reset(redisTemplate, hashOperations);
    }

    @Test
    @DisplayName("Verify that a key exists.")
    void shouldCheckAKeyExistsInRedis() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.hasKey(anyString(), anyString())).thenReturn(Boolean.TRUE);

        var keyExist = redisHashCacheService.exists(REDIS_THE_KEY);

        assertThat(keyExist)
            .isNotNull()
            .isTrue();

        verify(redisTemplate).opsForHash();
        verify(hashOperations).hasKey(anyString(), anyString());
    }

    @Test
    @DisplayName("Verify that a key does not exist.")
    void shouldCheckAKeyDoesNotExistInRedis() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.hasKey(anyString(), anyString())).thenReturn(Boolean.FALSE);

        var keyExist = redisHashCacheService.exists(REDIS_THE_KEY);

        assertThat(keyExist)
            .isNotNull()
            .isFalse();

        verify(redisTemplate).opsForHash();
        verify(hashOperations).hasKey(anyString(), anyString());
    }

    @Test
    @DisplayName("Verify that a key/value is inserted.")
    void shouldInsertAStringIntoRedisAssumesKeyHoldsHash() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        doNothing().when(hashOperations).put(anyString(), anyString(), anyString());

        redisHashCacheService.insert(REDIS_THE_KEY, REDIS_THE_INFO_SAVED);

        verify(redisTemplate).opsForHash();
        verify(hashOperations).put(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Verify we can add a bulk key/value data.")
    void shouldPerformABulkInsertIntoRedis() {
        var keyValueMap = expectedKeyValueMap();

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        doNothing().when(hashOperations).putAll(anyString(), anyMap());

        redisHashCacheService.multiInsert(keyValueMap);

        verify(redisTemplate).opsForHash();
        verify(hashOperations).putAll(anyString(), anyMap());
    }

    @Test
    @DisplayName("Verify that a key/value is retrieved.")
    void shouldRetrieveAStringFromRedis() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(REDIS_THE_INFO_SAVED);

        var retrievedInfo = redisHashCacheService.retrieve(REDIS_THE_KEY);

        assertThat(retrievedInfo)
            .isNotNull()
            .isEqualTo(REDIS_THE_INFO_SAVED);

        verify(redisTemplate).opsForHash();
        verify(hashOperations).get(anyString(), anyString());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"*", "ITEM*", "*ITEM*"})
    @DisplayName("Verify we are able to retrieve a bulk of keys from Redis given a key pattern.")
    void givenAKeyPatternThenRetrieveKeyListFromRedis(String keyPattern) {
        var multipleKeys = assignMultipleKeys();
        var cursor = buildScanCursor();

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.scan(anyString(), any())).thenReturn(cursor);

        var retrievedStringList = redisHashCacheService.multiRetrieveKeyList(keyPattern);

        assertThat(retrievedStringList)
            .hasSize(INT_THREE)
            .containsExactlyElementsOf(multipleKeys);

        verify(redisTemplate).opsForHash();
        verify(hashOperations).scan(anyString(), any());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"*", "ITEM*", "*ITEM*"})
    @DisplayName("Verify we are able to retrieve a bulk of data from Redis given a key pattern.")
    void givenAKeyPatternThenRetrieveAListOfStringFromRedis(String keyPattern) {
        var multipleValues = assignMultipleValues();
        var cursor = buildScanCursor();

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.scan(anyString(), any())).thenReturn(cursor);

        var retrievedStringList = redisHashCacheService.multiRetrieveList(keyPattern);

        assertThat(retrievedStringList)
            .isNotNull()
            .hasSize(INT_THREE)
            .containsExactlyElementsOf(multipleValues);

        verify(redisTemplate).opsForHash();
        verify(hashOperations).scan(anyString(), any());
    }

    @Test
    @DisplayName("Verify we are able to retrieve a bulk of data from Redis given a list of keys to look for.")
    void givenAListOfKeysThenRetrieveAListOfStringFromRedis() {
        var multipleKeys = assignMultipleKeys();
        var multipleValues = assignMultipleValuesAsObjectList();
        var multipleValuesExpected = assignMultipleValues();

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.multiGet(anyString(), anyList())).thenReturn(multipleValues);

        var retrievedStringList = redisHashCacheService.multiRetrieveList(multipleKeys);

        assertThat(retrievedStringList)
            .isNotNull()
            .hasSize(INT_THREE)
            .containsExactlyInAnyOrderElementsOf(multipleValuesExpected);

        verify(redisTemplate).opsForHash();
        verify(hashOperations).multiGet(anyString(), anyList());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"*", "ITEM*", "*ITEM*"})
    @DisplayName("Verify we are able to retrieve a map containing the key/value pairs from Redis using a key pattern.")
    void givenAKeyPatternThenRetrieveAMapWithKeyValueDataFromRedis(String keyPattern) {
        var expectedKeyValueMap = expectedKeyValueMap();
        var cursor = buildScanCursor();

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.scan(anyString(), any())).thenReturn(cursor);

        var retrievedStringMap = redisHashCacheService.multiRetrieveMap(keyPattern);

        assertThat(retrievedStringMap)
            .isNotNull()
            .hasSize(INT_THREE)
            .containsExactlyInAnyOrderEntriesOf(expectedKeyValueMap);

        verify(redisTemplate).opsForHash();
        verify(hashOperations).scan(anyString(), any());
    }

    @Test
    @DisplayName("Verify we are able to retrieve a map containing the key/value pairs from Redis using a collection containing the keys to look for.")
    void givenListOfKeysThenRetrieveAMapWithKeyValueDataFromRedis() {
        var multipleKeys = assignMultipleKeys();
        var multipleValues = assignMultipleValuesAsObjectList();
        var expectedKeyValueMap = expectedKeyValueMap();

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.multiGet(anyString(), anyList())).thenReturn(multipleValues);

        var retrievedKeyValueMap = redisHashCacheService.multiRetrieveMap(multipleKeys);

        assertThat(retrievedKeyValueMap)
            .isNotNull()
            .hasSize(INT_THREE)
            .containsExactlyInAnyOrderEntriesOf(expectedKeyValueMap);

        verify(redisTemplate).opsForHash();
        verify(hashOperations).multiGet(anyString(), anyList());
    }

    @Test
    @DisplayName("Verify we are able to retrieve a map containing the key/value pairs from Redis using a collection containing the keys to look for, but some keys are not found, whose values are null in the response from Redis but filter out by the service.")
    void givenListOfKeysThenRetrieveAMapWithKeyValueDataFromRedisDiscardingNotFoundKeys() {
        var multipleKeys = assignMultipleKeys();
        var multipleValues = assignMultipleValuesWithNullAsObjectList();
        var expectedKeyValueMap = expectedKeyValueExcludeNullItemMap();

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.multiGet(anyString(), anyList())).thenReturn(multipleValues);

        var retrievedKeyValueMap = redisHashCacheService.multiRetrieveMap(multipleKeys);

        assertThat(retrievedKeyValueMap)
            .isNotNull()
            .hasSize(INT_TWO)
            .containsExactlyInAnyOrderEntriesOf(expectedKeyValueMap);

        verify(redisTemplate).opsForHash();
        verify(hashOperations).multiGet(anyString(), anyList());
    }

    @Test
    @DisplayName("Verify we are able to retrieve a map containing all of the key/value pairs from Redis.")
    void shouldRetrieveAllMapWithKeyValueDataFromRedis() {
        var expectedKeyValueMap = expectedKeyValueMap();
        var keyValueMap = expectedKeyValueMapAsObjects();

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries(anyString())).thenReturn(keyValueMap);

        var retrievedStringMap = redisHashCacheService.multiRetrieveMap();

        assertThat(retrievedStringMap)
            .isNotNull()
            .hasSize(INT_THREE)
            .containsExactlyInAnyOrderEntriesOf(expectedKeyValueMap);

        verify(redisTemplate).opsForHash();
        verify(hashOperations).entries(anyString());
    }

    @Test
    @DisplayName("Verify we can remove a key from Redis")
    void givenAKeyAndItDoesNotExistThenRemoveReturnsFalse() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.delete(anyString(), anyString())).thenReturn(LONG_ZERO);

        var isRemoved = redisHashCacheService.delete(REDIS_THE_KEY);

        assertThat(isRemoved)
            .isNotNull()
            .isFalse();

        verify(redisTemplate).opsForHash();
        verify(hashOperations).delete(anyString(), anyString());
    }

    @Test
    @DisplayName("Verify we cannot remove a key from Redis because key is no present.")
    void givenAKeyThenRemove() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.delete(anyString(), anyString())).thenReturn(LONG_ONE);

        var isRemoved = redisHashCacheService.delete(REDIS_THE_KEY);

        assertThat(isRemoved)
            .isNotNull()
            .isTrue();

        verify(redisTemplate).opsForHash();
        verify(hashOperations).delete(anyString(), anyString());
    }

    @Test
    @DisplayName("Verify we can remove a key from Redis")
    void givenAListOfKeysThenBulkRemove() {
        var multipleKeys = assignMultipleValues();

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.delete(REDIS_HASH, multipleKeys.toArray())).thenReturn(LONG_THREE);

        var numKeysRemoved = redisHashCacheService.delete(multipleKeys);

        assertThat(numKeysRemoved)
            .isNotNull()
            .isEqualTo(LONG_THREE);

        verify(redisTemplate).opsForHash();
        verify(hashOperations).delete(REDIS_HASH, multipleKeys.toArray());
    }

    @Test
    @DisplayName("Verify we retrieve all the items in the Cache Hash")
    void shouldCountAllItemsInCacheHash() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.size(anyString())).thenReturn(LONG_THREE);

        var numItems = redisHashCacheService.count();

        assertThat(numItems)
            .isNotNull()
            .isEqualTo(LONG_THREE);

        verify(redisTemplate).opsForHash();
        verify(hashOperations).size(anyString());
    }

    @Test
    @DisplayName("Verify we retrieve selected items in the Cache Hash")
    void shouldCountSelectedItemsInCacheHash() {
        var cursor = buildScanCursor();

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.scan(anyString(), any())).thenReturn(cursor);

        var numItems = redisHashCacheService.count(KEY_PATTERN_AA);

        assertThat(numItems)
            .isNotNull()
            .isEqualTo(LONG_THREE);

        verify(redisTemplate).opsForHash();
        verify(hashOperations).scan(anyString(), any());
    }

    private Set<String> assignMultipleKeys() {
        // We need the key set to be in the order of insertion.
        var keySet = new LinkedHashSet<String>();

        keySet.add(REDIS_KEY_ITEM_1234);
        keySet.add(REDIS_KEY_ITEM_4567);
        keySet.add(REDIS_KEY_ITEM_8901);

        return keySet;
    }

    private List<String> assignMultipleValues() {
        return List.of(REDIS_VALUE_ITEM_1234, REDIS_VALUE_ITEM_4567, REDIS_VALUE_ITEM_8901);
    }

    private List<Object> assignMultipleValuesAsObjectList() {
        return List.of(REDIS_VALUE_ITEM_1234, REDIS_VALUE_ITEM_4567, REDIS_VALUE_ITEM_8901);
    }

    private List<Object> assignMultipleValuesWithNullAsObjectList() {
        var valueList = new ArrayList<>();

        valueList.add(REDIS_VALUE_ITEM_1234);
        valueList.add(null);
        valueList.add(REDIS_VALUE_ITEM_8901);

        return valueList;
    }

    private Map<String, String> expectedKeyValueMap() {
        return createKeyValueMap();
    }

    private Map<String, String> expectedKeyValueExcludeNullItemMap() {
        // We need the key map to be in the order of insertion.
        var expectedMap = new LinkedHashMap<String, String>();

        expectedMap.put(REDIS_KEY_ITEM_1234, REDIS_VALUE_ITEM_1234);
        expectedMap.put(REDIS_KEY_ITEM_8901, REDIS_VALUE_ITEM_8901);

        return expectedMap;
    }

    private Map<Object, Object> expectedKeyValueMapAsObjects() {
        return createKeyValueMapAsObjects();
    }

    private LinkedHashMap<String, String> createKeyValueMap() {
        // We need the key map to be in the order of insertion.
        var expectedMap = new LinkedHashMap<String, String>();

        expectedMap.put(REDIS_KEY_ITEM_1234, REDIS_VALUE_ITEM_1234);
        expectedMap.put(REDIS_KEY_ITEM_4567, REDIS_VALUE_ITEM_4567);
        expectedMap.put(REDIS_KEY_ITEM_8901, REDIS_VALUE_ITEM_8901);

        return expectedMap;
    }

    private LinkedHashMap<Object, Object> createKeyValueMapAsObjects() {
        // We need the key map to be in the order of insertion.
        var expectedMap = new LinkedHashMap<>();

        expectedMap.put(REDIS_KEY_ITEM_1234, REDIS_VALUE_ITEM_1234);
        expectedMap.put(REDIS_KEY_ITEM_4567, REDIS_VALUE_ITEM_4567);
        expectedMap.put(REDIS_KEY_ITEM_8901, REDIS_VALUE_ITEM_8901);

        return expectedMap;
    }

    private Cursor<Map.Entry<Object, Object>> buildScanCursor() {
        var values = new LinkedList<ScanIteration<Map.Entry<Object, Object>>>();

        values.add(
            createIteration(
                INT_ZERO,
                Map.entry(REDIS_KEY_ITEM_1234, REDIS_VALUE_ITEM_1234),
                Map.entry(REDIS_KEY_ITEM_4567, REDIS_VALUE_ITEM_4567),
                Map.entry(REDIS_KEY_ITEM_8901, REDIS_VALUE_ITEM_8901))
        );

        return initCursor(values);
    }

    private CapturingCursor initCursor(Queue<ScanIteration<Map.Entry<Object, Object>>> values) {
        var cursor = new CapturingCursor(values);

        cursor.open();

        return cursor;
    }

    @SafeVarargs
    private ScanIteration<Map.Entry<Object, Object>> createIteration(long cursorId, Map.Entry<Object, Object>... values) {
        return
            new ScanIteration<>(
                cursorId,
                values.length > INT_ZERO
                    ? Arrays.asList(values)
                    : Collections.emptyList()
            );
    }

    private class CapturingCursor extends ScanCursor<Map.Entry<Object, Object>> {

        private Queue<ScanIteration<Map.Entry<Object, Object>>> values;
        private Stack<Long> cursors;

        CapturingCursor(Queue<ScanIteration<Map.Entry<Object, Object>>> values) {
            this.values = values;
        }

        @Override
        protected ScanIteration<Map.Entry<Object, Object>> doScan(long cursorId, ScanOptions options) {

            if (cursors == null) {
                cursors = new Stack<>();
            }

            this.cursors.push(cursorId);

            return this.values.poll();
        }
    }
}
