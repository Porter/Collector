package com.porter.collector.model;

import com.google.common.collect.ImmutableMap;
import com.porter.collector.model.Values.MyFloat;
import com.porter.collector.model.Values.ValueType;

import java.util.Map;

public enum ValueTypes {
    FLOAT, INT;

    public final static Map<ValueTypes, ValueType<?>> map = ImmutableMap.of(
            FLOAT, new MyFloat()
    );
}
