package com.porter.collector.util;

import com.google.common.collect.ImmutableMap;
import com.porter.collector.model.ValueTypes;
import com.porter.collector.values.CustomType;
import com.porter.collector.values.MyInteger;
import com.porter.collector.values.MyString;
import com.porter.collector.values.ValueType;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class ValueUtilTest {

    @Test
    public void emptyInfo() {
        CustomType customType = new CustomType(ImmutableMap.of(
                "num", ValueTypes.INT,
                "s", ValueTypes.STRING,
                "cus", ValueTypes.CUSTOM
        ));

        Map<String, ValueType> empty = ValueUtil.emptyInfo(customType);
        Map<String, ValueType> expected = ImmutableMap.of(
                "num", new MyInteger(0),
                "s", new MyString(""),
                "cus", new CustomType()
        );

        assertEquals(expected, empty);
    }
}