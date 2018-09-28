package com.porter.collector.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.porter.collector.model.Source;
import com.porter.collector.model.ValueTypes;
import com.porter.collector.values.CustomType;
import com.porter.collector.values.ValueType;

import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.Map;

public class ValueValidator {

    private final ObjectMapper objectMapper;

    public ValueValidator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Map<String, ValueType> parseValues(Map<String, String> values, CustomType type) {
        Map<String, ValueType> map = new HashMap<>();
        type.getTypes().forEach((key, valueTypes) -> {
            ValueType valueType = parseValue(values.get(key), valueTypes);
            map.put(key, valueType);
        });

        return map;
    }

    public ValueType parseValue(String value, ValueTypes types) {
        try {
            return ValueTypes.getMap().get(types).parse(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isValid(ValueTypes valueTypes, String value) {
        ValueType valueType = ValueTypes.getMap().get(valueTypes);
        return isValid(valueType, value);
    }

    public boolean isValid(ValueType valueType, String value) {
        return valueType.isValid(value);
    }

    public boolean isCustomTypeValid(CustomType customType, Map<String, String> value) {
        if (!customType.getTypes().keySet().equals(value.keySet())) { return false; }

        Map<String, ValueTypes> typeMap = customType.getTypes();
        return value
                .entrySet()
                .stream()
                .allMatch(entry -> isValid(typeMap.get(entry.getKey()), entry.getValue()));
    }

    public boolean isTypeValid(ValueTypes type, Map<String, String> value) {
        String amount = value.get("amount");
        return amount != null && isValid(type, amount);
    }

    public String stringRepr(String value, ValueTypes types) {
        return parseValue(value, types).stringify();
    }

    public String stringRepr(Map<String, ValueType> values) {
        Map<String, String> map = new HashMap<>();
        values.forEach((key, valueType) -> map.put(key, valueType.stringify()));

        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String stringRepr(Map<String, String> values, CustomType customType) {
        return stringRepr(parseValues(values, customType));
    }

    public String stringRepr(Source source, Map<String, String> value) {
        if (hasCustomType(source)) {
            return stringRepr(value, source.customType());
        } else {
            return stringRepr(value.get("amount"), source.type());
        }
    }

    public boolean isValid(Source source, Map<String, String> value) {
        if (hasCustomType(source)) {
            return isCustomTypeValid(source.customType(), value);
        }
        else {
            return isTypeValid(source.type(), value);
        }
    }

    public Map<String, String> fromMultivaluedMap(MultivaluedMap<String, String> map) throws IllegalArgumentException {
        Map<String, String> singleValuedMap = new HashMap<>();
        map.forEach((key, value) -> {
            if (value.size() != 1) {
                throw new IllegalArgumentException("Multivaluedmap should be convertable to normal");
            }

            singleValuedMap.put(key, value.get(0));
        });

        return singleValuedMap;
    }

    private boolean hasCustomType(Source source) {
        if ((source.type() == null) == (source.customType() == null)) {
            throw new IllegalStateException("Source: " + source + "must have exactly one of type or custom type");
        }
        return source.type() == null;
    }

    public Map<String, ValueType> emptyInfo(CustomType customType) {
        Map<String, ValueType> map = new HashMap<>();
        customType.getTypes().forEach((string, valueTypes) -> {
            map.put(string, ValueTypes.getMap().get(valueTypes).zero());
        });

        return map;
    }

    public String toJson(Map<String, String> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
