package com.porter.collector.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.porter.collector.model.Values.MyFloat;
import com.porter.collector.model.Values.MyInteger;
import com.porter.collector.model.Values.ValueType;

import java.util.Map;

public enum ValueTypes {
    FLOAT, INT;

    public static Map<ValueTypes, ValueType<?>> getMap() {
        return map;
    }

    public static void register(ValueTypes type, ValueType<?> typeObj) {
        map = ImmutableMap.<ValueTypes, ValueType<?>>builder().putAll(map).put(type, typeObj).build();
    }
    private static Map<ValueTypes, ValueType<?>> map = ImmutableMap.of(
            FLOAT, new MyFloat(),
            INT, new MyInteger()
    );
}
