/*----------------------------------------------------------------------------*/
/* Source File:   REDISREGIONCACHEINTEGERKEYSERVICETEST.JAVA                  */
/* Copyright (c), 2025 Acme                                                   */
/*----------------------------------------------------------------------------*/
/*-----------------------------------------------------------------------------
 History
 Feb.17/2025  COQ  File created.
 -----------------------------------------------------------------------------*/
package com.acme.service.user.service;

import static com.acme.service.user.common.consts.GlobalConstants.INT_THREE;
import static com.acme.service.user.common.consts.GlobalConstants.INT_TWO;
import static com.acme.service.user.common.consts.GlobalConstants.LONG_THREE;
import static com.acme.service.user.common.consts.GlobalConstants.LONG_TWO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * Unit tests for {@link RedisRegionCacheService} using an Integer Key to cover this case when working with keys.
 * <br/><br/>
 * <b>NOTE:</b>This Unit test file is based on {@link RedisRegionCacheServiceTest}, thus any new test added there
 * should be added here as well to have unit tests in sync.
 *
 * @author COQ - Carlos Adolfo Ortiz Q.
 */
@ExtendWith(MockitoExtension.class)
class RedisRegionCacheIntegerKeyServiceTest {
    private static final String REDIS_REGION = "region";
    private static final String REDIS_THE_INFO_SAVED = "The Info saved";
    private static final String REDIS_VALUE_4567 = "VALUE4567";
    private static final String REDIS_VALUE_8901 = "VALUE8901";
    private static final String REDIS_VALUE_1234 = "VALUE1234";
    private static final String KEY_PATTERN_AA = "AA*";

    private static final Integer REDIS_THE_KEY = 123;
    private static final Integer REDIS_KEY_1234 = 1234;
    private static final Integer REDIS_KEY_4567 = 4567;
    private static final Integer REDIS_KEY_8901 = 8901;
    private static final Integer REDIS_KEY_12345 = 12345;
    private static final Integer REDIS_KEY_56789 = 56789;

    @Mock
    private RedisTemplate<Integer, String> redisTemplate;

    @Mock
    private ValueOperations<Integer, String> valueOperations;
    private RedisRegionCacheService<Integer, String> redisRegionCacheService;

    @BeforeEach
    void beforeEach() {
        redisRegionCacheService = new RedisRegionCacheService<>(REDIS_REGION, redisTemplate);
        reset(redisTemplate, valueOperations);
    }

    @Test
    @DisplayName("Verify that a key exists.")
    void shouldCheckAKeyExistsInRedis() {
        when(redisTemplate.hasKey(anyInt())).thenReturn(Boolean.TRUE);

        var keyExist = redisRegionCacheService.exists(REDIS_THE_KEY);

        assertThat(keyExist)
            .isNotNull()
            .isTrue();

        verify(redisTemplate).hasKey(anyInt());
    }

    @Test
    @DisplayName("Verify that a key does not exist.")
    void shouldCheckAKeyDoesNotExistInRedis() {
        when(redisTemplate.hasKey(anyInt())).thenReturn(Boolean.FALSE);

        var keyExist = redisRegionCacheService.exists(REDIS_THE_KEY);

        assertThat(keyExist)
            .isNotNull()
            .isFalse();

        verify(redisTemplate).hasKey(anyInt());
    }

    @Test
    @DisplayName("Verify that a key/value is inserted.")
    void shouldInsertAStringIntoRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyInt(), anyString());

        redisRegionCacheService.insert(REDIS_THE_KEY, REDIS_THE_INFO_SAVED);

        verify(redisTemplate).opsForValue();
        verify(valueOperations).set(anyInt(), anyString());
    }

    @Test
    @DisplayName("Verify that a key/value is inserted. This tests cover a case when 'buildRegionKey' has the region set already.")
    void shouldInsertAStringIntoRedisAssumesKeyHoldsRegion() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyInt(), anyString());

        redisRegionCacheService.insert(REDIS_THE_KEY, REDIS_THE_INFO_SAVED);

        verify(redisTemplate).opsForValue();
        verify(valueOperations).set(anyInt(), anyString());
    }

    @Test
    @DisplayName("Verify we can add a bulk key/value data.")
    void shouldPerformABulkInsertIntoRedis() {
        var keyValueMap = expectedKeyValueMap();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).multiSet(anyMap());

        redisRegionCacheService.multiInsert(keyValueMap);

        verify(redisTemplate).opsForValue();
        verify(valueOperations).multiSet(anyMap());
    }

    @Test
    @DisplayName("Verify that a key/value is retrieved.")
    void shouldRetrieveAStringFromRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(any())).thenReturn(REDIS_THE_INFO_SAVED);

        var retrievedInfo = redisRegionCacheService.retrieve(REDIS_THE_KEY);

        assertThat(retrievedInfo)
            .isNotNull()
            .isEqualTo(REDIS_THE_INFO_SAVED);

        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(any());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"*", "ITEM*", "*ITEM*"})
    @DisplayName("Verify we are able to retrieve a bulk of keys from Redis given a key pattern.")
    void givenAKeyPatternThenRetrieveKeyListFromRedis(String keyPattern) {
        var multipleKeys = assignMultipleKeys();

        when(redisTemplate.keys(any())).thenReturn(multipleKeys);

        var retrievedStringList = redisRegionCacheService.multiRetrieveKeyList(keyPattern);

        assertThat(retrievedStringList)
            .hasSize(INT_THREE)
            .containsExactlyElementsOf(multipleKeys);

        verify(redisTemplate).keys(any());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"*", "ITEM*", "*ITEM*"})
    @DisplayName("Verify we are able to retrieve a bulk of data from Redis given a key pattern.")
    void givenAKeyPatternThenRetrieveAListOfStringFromRedis(String keyPattern) {
        var multipleKeys = assignMultipleKeys();
        var multipleValues = assignMultipleValues();

        when(redisTemplate.keys(any())).thenReturn(multipleKeys);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.multiGet(anySet())).thenReturn(multipleValues);

        var retrievedStringList = redisRegionCacheService.multiRetrieveList(keyPattern);

        assertThat(retrievedStringList)
            .isNotNull()
            .hasSize(INT_THREE)
            .containsExactlyElementsOf(multipleValues);

        verify(redisTemplate).keys(any());
        verify(redisTemplate).opsForValue();
        verify(valueOperations).multiGet(anySet());
    }

    @Test
    @DisplayName("Verify we are able to retrieve a bulk of data from Redis given a list of keys to look for.")
    void givenAListOfKeysThenRetrieveAListOfStringFromRedis() {
        var multipleKeys = assignMultipleKeys();
        var multipleValues = assignMultipleValues();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.multiGet(anyList())).thenReturn(multipleValues);

        var retrievedStringList = redisRegionCacheService.multiRetrieveList(multipleKeys);

        assertThat(retrievedStringList)
            .isNotNull()
            .hasSize(INT_THREE)
            .containsExactlyInAnyOrderElementsOf(multipleValues);

        verify(redisTemplate).opsForValue();
        verify(valueOperations).multiGet(anyList());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"*", "ITEM*", "*ITEM*"})
    @DisplayName("Verify we are able to retrieve a map containing the key/value pairs from Redis using a key pattern.")
    void givenAKeyPatternThenRetrieveAMapWithKeyValueDataFromRedis(String keyPattern) {
        var keyList = assignMultipleKeys();
        var valueList = assignMultipleValues();
        var expectedKeyValueMap = expectedKeyValueMap();

        when(redisTemplate.keys(any())).thenReturn(keyList);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.multiGet(anyList())).thenReturn(valueList);

        var retrievedStringMap = redisRegionCacheService.multiRetrieveMap(keyPattern);

        assertThat(retrievedStringMap)
            .isNotNull()
            .hasSize(INT_THREE)
            .containsExactlyInAnyOrderEntriesOf(expectedKeyValueMap);

        verify(redisTemplate).keys(any());
        verify(redisTemplate).opsForValue();
        verify(valueOperations).multiGet(anyList());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"*", "ITEM*", "*ITEM*"})
    @DisplayName("Verify we are able to retrieve a map containing the key/value pairs from Redis using a key pattern discarding not found keys (when its value is null).")
    void givenAKeyPatternThenRetrieveAMapWithKeyValueDataFromRedisDiscardingNotFoundKeys(String keyPattern) {
        var keyList = assignMultipleKeys();
        var valueList = assignMultipleValuesWithNullAsObjectList();
        var expectedKeyValueMap = expectedKeyValueExcludeNullItemMap();

        when(redisTemplate.keys(any())).thenReturn(keyList);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.multiGet(anyList())).thenReturn(valueList);

        var retrievedStringMap = redisRegionCacheService.multiRetrieveMap(keyPattern);

        assertThat(retrievedStringMap)
            .isNotNull()
            .hasSize(INT_TWO)
            .containsExactlyInAnyOrderEntriesOf(expectedKeyValueMap);

        verify(redisTemplate).keys(any());
        verify(redisTemplate).opsForValue();
        verify(valueOperations).multiGet(anyList());
    }

    @Test
    @DisplayName("Verify we are able to retrieve a map containing the key/value pairs from Redis using a collection containing the keys to look for.")
    void givenListOfKeysThenRetrieveAMapWithKeyValueDataFromRedis() {
        var multipleKeys = assignMultipleKeys();
        var multipleValues = assignMultipleValues();
        var expectedKeyValueMap = expectedKeyValueMap();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.multiGet(anyList())).thenReturn(multipleValues);

        var retrievedKeyValueMap = redisRegionCacheService.multiRetrieveMap(multipleKeys);

        assertThat(retrievedKeyValueMap)
            .isNotNull()
            .hasSize(INT_THREE)
            .containsExactlyInAnyOrderEntriesOf(expectedKeyValueMap);

        verify(redisTemplate).opsForValue();
        verify(valueOperations).multiGet(anyList());
    }

    @Test
    @DisplayName("Verify we are able to retrieve a map containing the key/value pairs from Redis using a collection containing the keys to look for, discarding not found keys (which value is null) and discarded by the service.")
    void givenListOfKeysThenRetrieveAMapWithKeyValueDataFromRedisDiscardingNotFoundKeys() {
        var multipleKeys = assignMultipleKeys();
        var multipleValues = assignMultipleValuesWithNullAsObjectList();
        var expectedKeyValueMap = expectedKeyValueExcludeNullItemMap();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.multiGet(anyList())).thenReturn(multipleValues);

        var retrievedKeyValueMap = redisRegionCacheService.multiRetrieveMap(multipleKeys);

        assertThat(retrievedKeyValueMap)
            .isNotNull()
            .hasSize(INT_TWO)
            .containsExactlyInAnyOrderEntriesOf(expectedKeyValueMap);

        verify(redisTemplate).opsForValue();
        verify(valueOperations).multiGet(anyList());
    }

    @Test
    @DisplayName("Verify we are able to retrieve a map containing all of the key/value pairs from Redis.")
    void shouldRetrieveAllMapWithKeyValueDataFromRedis() {
        var keyList = assignMultipleKeys();
        var valueList = assignMultipleValues();
        var expectedKeyValueMap = expectedKeyValueMap();

        when(redisTemplate.keys(any())).thenReturn(keyList);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.multiGet(anyList())).thenReturn(valueList);

        var retrievedStringMap = redisRegionCacheService.multiRetrieveMap();

        assertThat(retrievedStringMap)
            .isNotNull()
            .hasSize(INT_THREE)
            .containsExactlyInAnyOrderEntriesOf(expectedKeyValueMap);

        verify(redisTemplate).keys(any());
        verify(redisTemplate).opsForValue();
        verify(valueOperations).multiGet(anyList());
    }

    @Test
    @DisplayName("Verify we can remove a key from Redis")
    void givenAKeyThenRemove() {
        when(redisTemplate.delete(anyInt())).thenReturn(Boolean.TRUE);

        var isRemoved = redisRegionCacheService.delete(REDIS_THE_KEY);

        assertThat(isRemoved)
            .isNotNull()
            .isTrue();

        verify(redisTemplate).delete(anyInt());
    }

    @Test
    @DisplayName("Verify we can remove a key from Redis")
    void givenAListOfKeysThenBulkRemove() {
        var multipleKeys = assignMultipleKeys();

        when(redisTemplate.delete(anyList())).thenReturn(LONG_THREE);

        var numKeysRemoved = redisRegionCacheService.delete(multipleKeys);

        assertThat(numKeysRemoved)
            .isNotNull()
            .isEqualTo(LONG_THREE);

        verify(redisTemplate).delete(anyList());
    }

    @Test
    @DisplayName("Verify we retrieve all the items in the Cache Region")
    void shouldCountAllItemsInCacheRegion() {
        var multipleKeys = assignMultipleKeys();

        when(redisTemplate.keys(any())).thenReturn(multipleKeys);

        var numItems = redisRegionCacheService.count();

        assertThat(numItems)
            .isNotNull()
            .isEqualTo(LONG_THREE);

        verify(redisTemplate).keys(any());
    }

    @Test
    @DisplayName("Verify we retrieve selected items in the Cache Region")
    void shouldCountSelectedItemsInCacheRegion() {
        var multipleKeys = assignMultipleItemWKeys();

        when(redisTemplate.keys(any())).thenReturn(multipleKeys);

        var numItems = redisRegionCacheService.count(KEY_PATTERN_AA);

        assertThat(numItems)
            .isNotNull()
            .isEqualTo(LONG_TWO);

        verify(redisTemplate).keys(any());
    }

    private Set<Integer> assignMultipleKeys() {
        // We need the key set to be in the order of insertion.
        var keySet = new LinkedHashSet<Integer>();

        keySet.add(REDIS_KEY_1234);
        keySet.add(REDIS_KEY_4567);
        keySet.add(REDIS_KEY_8901);

        return keySet;
    }

    private List<String> assignMultipleValues() {
        return List.of(REDIS_VALUE_1234, REDIS_VALUE_4567, REDIS_VALUE_8901);
    }

    private List<String> assignMultipleValuesWithNullAsObjectList() {
        var valueList = new ArrayList<String>();

        valueList.add(REDIS_VALUE_1234);
        valueList.add(null);
        valueList.add(REDIS_VALUE_8901);

        return valueList;
    }

    private Set<Integer> assignMultipleItemWKeys() {
        // We need the key set to be in the order of insertion.
        var keySet = new LinkedHashSet<Integer>();

        keySet.add(REDIS_KEY_12345);
        keySet.add(REDIS_KEY_56789);

        return keySet;
    }

    private Map<Integer, String> expectedKeyValueMap() {
        return createKeyValueMap();
    }

    private Map<Integer, String> expectedKeyValueExcludeNullItemMap() {
        // We need the key map to be in the order of insertion.
        var expectedMap = new LinkedHashMap<Integer, String>();

        expectedMap.put(REDIS_KEY_1234, REDIS_VALUE_1234);
        expectedMap.put(REDIS_KEY_8901, REDIS_VALUE_8901);

        return expectedMap;
    }

    private LinkedHashMap<Integer, String> createKeyValueMap() {
        // We need the key map to be in the order of insertion.
        var expectedMap = new LinkedHashMap<Integer, String>();

        expectedMap.put(REDIS_KEY_1234, REDIS_VALUE_1234);
        expectedMap.put(REDIS_KEY_4567, REDIS_VALUE_4567);
        expectedMap.put(REDIS_KEY_8901, REDIS_VALUE_8901);

        return expectedMap;
    }
}
