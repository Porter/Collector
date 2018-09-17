package com.porter.collector.values;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.porter.collector.model.CustomTypeDeserializer;
import com.porter.collector.model.CustomTypeSerializer;
import com.porter.collector.model.ValueTypes;
import io.dropwizard.jackson.Jackson;
import org.glassfish.jersey.internal.inject.Custom;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonSerialize(using = CustomTypeSerializer.class)
@JsonDeserialize(using = CustomTypeDeserializer.class)
public class CustomType implements ValueType<CustomType> {

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
    public CustomType combine(CustomType other) {
        return null;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomType)) return false;
        CustomType that = (CustomType) o;
        return Objects.equals(types, that.types);
    }

    @Override
    public int hashCode() {
        return Objects.hash(types);
    }
}
