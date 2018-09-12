package com.porter.collector.model.Values;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.porter.collector.model.ValueTypes;
import io.dropwizard.jackson.Jackson;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class CustomType implements ValueType {

    private Map<String, ValueTypes> types;

    public CustomType() { }

    public CustomType(Map<String, ValueTypes> types) {
        this.types = types;
    }

    @Override
    public CustomType parse(String json) throws Exception {
        ObjectMapper mapper = Jackson.newObjectMapper();
        TypeReference<HashMap<String, Integer>> typeReference = new TypeReference<HashMap<String, Integer>>() {};

        try {
            HashMap<String, Integer> map = mapper.readValue(json.getBytes(), typeReference);
            HashMap<String, ValueTypes> newTypes = new HashMap<>();
            map.forEach((key, index) -> newTypes.put(key, ValueTypes.values()[index]));

            return new CustomType(newTypes);
        } catch (IOException e) {
            throw new ParseException("Invalid json: " + json, 0);
        }
    }

    @Override
    public String stringify() {
        Map<String, String> stringified = new HashMap<>();
        types.forEach((key, value) -> stringified.put(key, value.userFriendlyName()));
        ObjectMapper mapper = Jackson.newObjectMapper();
        try {
            return mapper.writeValueAsString(stringified);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public Map<String, ValueTypes> getTypes() {
        return types;
    }
}
