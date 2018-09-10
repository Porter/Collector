package com.porter.collector.parser;

import com.google.common.collect.ImmutableMap;
import com.porter.collector.model.ValueTypes;
import com.porter.collector.model.Values.ValueType;

import java.util.Map;

public class Tokens {

    public static Map<String, Function> getTokens() {
        return tokens;
    }

    public static void register(String name, Function function) {
        tokens = ImmutableMap.<String, Function>builder().putAll(tokens).put(name + "(", function).build();
    }

    private static Map<String, Function> tokens = ImmutableMap.of(
            "sum(", new Sum()
    );
}
