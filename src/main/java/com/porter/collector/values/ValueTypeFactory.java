package com.porter.collector.values;

import com.porter.collector.model.Value;
import com.porter.collector.model.ValueTypes;

import java.text.ParseException;

public class ValueTypeFactory {

    public static ValueType getFromValue(ValueTypes type, Value value) throws ParseException {
        try {
            return ValueTypes.getMap().get(type).parse(value.value());
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }
}
