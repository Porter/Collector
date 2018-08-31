package com.porter.collector.model.Values;

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

    private float _parse(String value) throws NumberFormatException {
        return Float.parseFloat(value);
    }

    @Override
    public MyFloat parse(String value) {
        try {
            return new MyFloat(_parse(value));
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String stringify() {
        return "" + value;
    }
}
