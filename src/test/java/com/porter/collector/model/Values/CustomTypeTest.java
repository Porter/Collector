package com.porter.collector.model.Values;

import com.google.common.collect.ImmutableMap;
import com.porter.collector.model.ValueTypes;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class CustomTypeTest {

    @Test
    public void create() throws Exception {
        CustomType type = new CustomType();
        CustomType parsed = type.parse("{\"amount\":0}");

        Map<String, ValueTypes> expected = ImmutableMap.of("amount", ValueTypes.FLOAT);
        Map<String, ValueTypes> actual = parsed.getTypes();

        assertEquals(expected, actual);
    }

    @Test
    public void stringify() throws Exception {
        CustomType type = new CustomType();
        CustomType parsed = type.parse("{\"amount\":0}");

        String expected = "{\"amount\":\"Float\"}";
        String actual = parsed.stringify();

        assertEquals(expected, actual);
    }
}