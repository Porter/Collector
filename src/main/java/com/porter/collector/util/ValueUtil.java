package com.porter.collector.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.porter.collector.model.ValueTypes;
import com.porter.collector.values.CustomType;
import com.porter.collector.values.ValueType;
import io.dropwizard.jackson.Jackson;

import java.util.HashMap;
import java.util.Map;

public class ValueUtil {

    private static final ObjectMapper mapper = Jackson.newObjectMapper();

    public static String stringifyValues(Map<String, ValueType> values) {
        Map<String, String> map = new HashMap<>();
        values.forEach((key, valueType) -> map.put(key, valueType.stringify()));

        ObjectMapper mapper = Jackson.newObjectMapper();
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJson(Map<String, String> map) {
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, ValueType> emptyInfo(CustomType customType) {
        Map<String, ValueType> map = new HashMap<>();
        customType.getTypes().forEach((string, valueTypes) -> {
            map.put(string, ValueTypes.getMap().get(valueTypes).zero());
        });

        return map;
    }
}
