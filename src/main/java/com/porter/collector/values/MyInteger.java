package com.porter.collector.values;

public class MyInteger implements Addable<MyInteger>, ValueType<MyInteger> {

    private int value;

    public MyInteger(int value) {
        this.value = value;
    }

    public MyInteger() {
        this.value = 0;
    }

    public int getValue() {
        return value;
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
    public MyInteger combine(MyInteger other) {
        return this.add(other);
    }

    @Override
    public String stringify() {
        return "" + value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MyInteger)) { return false; }

        return ((MyInteger) o).value == value;
    }
}
