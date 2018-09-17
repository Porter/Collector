package com.porter.collector.parser;

import com.porter.collector.values.ValueType;

import java.util.Objects;

public class Constant implements Function {

    private final ValueType value;

    public Constant(ValueType value) {
        this.value = value;
    }

    @Override
    public ValueType exec(Args args) {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Constant)) return false;
        Constant constant = (Constant) o;
        return Objects.equals(value, constant.value);
    }

    @Override
    public String toString() {
        return "Constant{" +
                "value=" + value +
                '}';
    }
}
