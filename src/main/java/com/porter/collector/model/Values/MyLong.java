package com.porter.collector.model.Values;

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
    public String stringify() {
        return "" + value;
    }
}
