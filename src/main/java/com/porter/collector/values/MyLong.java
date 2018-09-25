package com.porter.collector.values;

public class MyLong implements Addable<MyLong>, ValueType<MyLong> {

    private long value;

    public MyLong(long value) {
        this.value = value;
    }

    @Override
    public MyLong add(MyLong other) {
        return new MyLong(value + other.value);
    }

    @Override
    public MyLong parse(String value) {
        return new MyLong(Long.parseLong(value));
    }

    @Override
    public MyLong combine(MyLong other) {
        return this.add(other);
    }

    @Override
    public String stringify() {
        return "" + value;
    }

    @Override
    public MyLong zero() {
        return new MyLong(0);
    }

    public long getValue() {
        return value;
    }
}
