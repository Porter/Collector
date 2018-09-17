package com.porter.collector.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.porter.collector.values.CustomType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomTypeSerializer extends JsonSerializer<CustomType> {

    @Override
    public void serialize(CustomType customType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {

        Map<String, Integer> map = new HashMap<>();
        customType.getTypes().forEach((key, value) -> map.put(key, value.ordinal()));

        jsonGenerator.writeObject(map);
    }
}
