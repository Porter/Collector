package com.porter.collector.values;

public class MyFloat implements Addable<MyFloat>, ValueType<MyFloat> {

    private float value;

    public MyFloat(float value) {
        this.value = value;
    }

    public MyFloat(String value) {
        this.value = _parse(value);
    }

    public MyFloat() {
        this.value = 0;
    }

    @Override
    public MyFloat add(MyFloat other) {
        return new MyFloat(value + other.value);
    }

    private float _parse(String value) {
        return Float.parseFloat(value);
    }

    @Override
    public MyFloat parse(String value) throws NumberFormatException {
        return new MyFloat(_parse(value));
    }

    @Override
    public MyFloat combine(MyFloat other) {
        return null;
    }

    @Override
    public String stringify() {
        return "" + value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MyFloat)) { return false; }
        return ((MyFloat) o).value == value;
    }
}
