package com.porter.collector.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.porter.collector.values.CustomType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomTypeDeserializer extends JsonDeserializer<CustomType> {

    @Override
    public CustomType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        TypeReference<HashMap<String, Integer>> typeReference = new TypeReference<HashMap<String, Integer>>() {};
        Map<String, Integer> map = jsonParser.readValueAs(typeReference);

        Map<String, ValueTypes> valueTypesMap = new HashMap<>();
        map.forEach((key, value) -> valueTypesMap.put(key, ValueTypes.values()[value]));

        return new CustomType(valueTypesMap);
    }
}
