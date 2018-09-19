package com.porter.collector.model;

import com.google.common.collect.ImmutableMap;
import com.porter.collector.values.*;

import java.util.Map;

public enum ValueTypes {
    FLOAT ("Float"), INT ("Integer"), CUSTOM ("Custom"), MONEY ("Dollars"), STRING ("String"), DATE ("Date");

    private final String userFriendlyName;

    ValueTypes(String userFriendlyName) {
        this.userFriendlyName = userFriendlyName;
    }

    public static Map<ValueTypes, ValueType<?>> getMap() {
        return map;
    }

    public static void register(ValueTypes type, ValueType<?> typeObj) {
        map = ImmutableMap.<ValueTypes, ValueType<?>>builder().putAll(map).put(type, typeObj).build();
    }
    private static Map<ValueTypes, ValueType<?>> map = ImmutableMap.of(
            FLOAT, new MyFloat(),
            INT, new MyInteger(),
            MONEY, new Money(),
            STRING, new MyString(),
            DATE, new MyDate()
    );

    public String userFriendlyName() {
        return userFriendlyName;
    }
}
