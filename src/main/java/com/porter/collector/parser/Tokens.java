package com.porter.collector.parser;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class Tokens {

    public static final Map<String, Function> tokens = ImmutableMap.of(
            "sum(", new Sum()
    );
}
