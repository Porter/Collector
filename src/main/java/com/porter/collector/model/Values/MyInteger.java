package com.porter.collector.model.Values;

public class MyInteger implements Addable<MyInteger>, ValueType<MyInteger> {

    private int value;

    public MyInteger(int value) {
        this.value = value;
    }

    @Override
    public MyInteger add(MyInteger other) {
        return new MyInteger(value + other.value);
    }

    @Override
    public MyInteger parse(String value) {
        return new MyInteger(Integer.parseInt(value));
    }

    @Override
    public String stringify() {
        return "" + value;
    }
}
