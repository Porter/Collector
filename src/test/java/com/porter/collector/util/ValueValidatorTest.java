package com.porter.collector.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.porter.collector.model.Source;
import com.porter.collector.model.ValueTypes;
import com.porter.collector.values.CustomType;
import io.dropwizard.jackson.Jackson;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static com.porter.collector.util.TestUtil.mockedMapWithKeySet;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ValueValidatorTest {

    private ObjectMapper mapper;
    private ValueValidator validator;

    @Before
    public void setUp() {
        mapper = spy(Jackson.newObjectMapper());
        validator = new ValueValidator(mapper);
    }

    @Test(expected = IllegalStateException.class)
    public void isValidNoTypes() throws Exception {
        Map<String, String> value = ImmutableMap.of("key", "2", "k2", "3");
        Source source = mock(Source.class);

        validator.isValid(source, value);
    }

    @Test(expected = IllegalStateException.class)
    public void isValidBothTypes() throws Exception {
        Map<String, String> value = ImmutableMap.of("key", "2", "k2", "3");
        CustomType customType = mock(CustomType.class);
        ValueTypes valueTypes = mock(ValueTypes.class);
        Source source = mock(Source.class);
        when(source.type()).thenReturn(valueTypes);
        when(source.customType()).thenReturn(customType);

        validator.isValid(source, value);
    }

    @Test
    public void isValidCustomType() throws Exception {
        ValueValidator mocked = mock(ValueValidator.class);

        Source source = mock(Source.class);
        CustomType customType = mock(CustomType.class);
        when(source.customType()).thenReturn(customType);

        Map<String, String> value = mock(Map.class);

        when(mocked.isValid(source, value)).thenCallRealMethod();
        mocked.isValid(source, value);

        verify(mocked).isCustomTypeValid(customType, value);
        verify(mocked, never()).isTypeValid(any(), any());
    }

    @Test
    public void isValidType() throws Exception {
        ValueValidator mocked = mock(ValueValidator.class);

        Source source = mock(Source.class);
        ValueTypes valueTypes = mock(ValueTypes.class);
        when(source.type()).thenReturn(valueTypes);

        Map<String, String> value = mock(Map.class);

        when(mocked.isValid(source, value)).thenCallRealMethod();
        mocked.isValid(source, value);

        verify(mocked, never()).isCustomTypeValid(any(), any());
        verify(mocked).isTypeValid(valueTypes, value);
    }

    @Test
    public void isValidCustomTypeTestDiffKeySets() throws Exception {
        CustomType customType = mock(CustomType.class);
        Map<String, ValueTypes> map = mockedMapWithKeySet(ImmutableSet.of("one", "two"));
        when(customType.getTypes()).thenReturn(map);
        Map<String, String> values = mockedMapWithKeySet(ImmutableSet.of("one", "notTwo"));

        assertFalse(validator.isCustomTypeValid(customType, values));
    }

    @Test
    public void isValidCustomTypeAllValid() throws Exception {
        CustomType customType = mock(CustomType.class);
        Map<String, ValueTypes> map = ImmutableMap.of("key1", ValueTypes.INT, "key2", ValueTypes.STRING);
        when(customType.getTypes()).thenReturn(map);
        Map<String, String> values = ImmutableMap.of("key1", "v1", "key2", "v2");

        ValueValidator mocked = mock(ValueValidator.class);

        when(mocked.isValid(ValueTypes.INT, "v1")).thenReturn(true);
        when(mocked.isValid(ValueTypes.STRING, "v2")).thenReturn(true);

        when(mocked.isCustomTypeValid(customType, values)).thenCallRealMethod();
        assertTrue(mocked.isCustomTypeValid(customType, values));
    }

    @Test
    public void isValidCustomTypeOneInvalid() throws Exception {
        CustomType customType = mock(CustomType.class);
        Map<String, ValueTypes> map = ImmutableMap.of("key1", ValueTypes.INT, "key2", ValueTypes.STRING);
        when(customType.getTypes()).thenReturn(map);
        Map<String, String> values = ImmutableMap.of("key1", "v1", "key2", "v2");

        ValueValidator mocked = mock(ValueValidator.class);

        when(mocked.isValid(ValueTypes.INT, "v1")).thenReturn(true);
        when(mocked.isValid(ValueTypes.STRING, "v2")).thenReturn(false);

        when(mocked.isCustomTypeValid(customType, values)).thenCallRealMethod();
        assertFalse(mocked.isCustomTypeValid(customType, values));
        verify(mocked).isValid(ValueTypes.STRING, "v2");
    }

    @Test
    public void isValidTypeMissingAmount() throws Exception {
        Map<String, String> value = ImmutableMap.of("notAmount", "val");
        ValueTypes type = mock(ValueTypes.class);
        assertFalse(validator.isTypeValid(type, value));
    }

    @Test
    public void isValidTypeNotValid() throws Exception {
        Map<String, String> value = ImmutableMap.of("amount", "val");
        ValueTypes type = mock(ValueTypes.class);

        ValueValidator mocked = mock(ValueValidator.class);
        when(mocked.isValid(type, "val")).thenReturn(false);

        when(mocked.isTypeValid(type, value)).thenCallRealMethod();
        assertFalse(mocked.isTypeValid(type, value));
        verify(mocked).isValid(type, "val");
    }

    @Test
    public void isValidTypeValid() throws Exception {
        Map<String, String> value = ImmutableMap.of("amount", "val");
        ValueTypes type = mock(ValueTypes.class);

        ValueValidator mocked = mock(ValueValidator.class);
        when(mocked.isValid(type, "val")).thenReturn(true);

        when(mocked.isTypeValid(type, value)).thenCallRealMethod();
        assertTrue(mocked.isTypeValid(type, value));
        verify(mocked).isValid(type, "val");
    }

    @Test(expected = IllegalStateException.class)
    public void stringReprNoTypeTest() throws Exception {
        Map<String, String> value = mock(Map.class);
        Source source = mock(Source.class);

        validator.stringRepr(source, value);
    }

    @Test(expected = IllegalStateException.class)
    public void stringReprBothTypeTest() throws Exception {
        CustomType customType = mock(CustomType.class);
        ValueTypes types = mock(ValueTypes.class);
        Map<String, String> value = mock(Map.class);
        Source source = mock(Source.class);
        when(source.type()).thenReturn(types);
        when(source.customType()).thenReturn(customType);

        validator.stringRepr(source, value);
    }

    @Test
    public void stringReprCustomTypeTest() throws Exception {
        CustomType customType = mock(CustomType.class);
        Map<String, String> values = mock(Map.class);
        Source source = mock(Source.class);
        when(source.customType()).thenReturn(customType);

        ValueValidator mocked = mock(ValueValidator.class);

        when(mocked.stringRepr(source, values)).thenCallRealMethod();
        mocked.stringRepr(source, values);
        verify(mocked).stringRepr(values, customType);
    }

    @Test
    public void stringReprTypeTest() throws Exception {
        Map<String, String> values = mock(Map.class);
        when(values.get("amount")).thenReturn("123p");
        ValueTypes types = mock(ValueTypes.class);
        Source source = mock(Source.class);
        when(source.type()).thenReturn(types);

        ValueValidator mocked = mock(ValueValidator.class);

        when(mocked.stringRepr(source, values)).thenCallRealMethod();
        mocked.stringRepr(source, values);
        verify(mocked).stringRepr("123p", types);
    }

}