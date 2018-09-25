package com.porter.collector.util;

import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtil {

    public static <K, V> Map<K, V> mockedMapWithKeySet(Set<K> keySet) {
        Map<K, V> map = mock(Map.class);
        when(map.keySet()).thenReturn(keySet);
        return map;
    }
}
