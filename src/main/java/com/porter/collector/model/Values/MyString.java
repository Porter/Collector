package com.porter.collector.model.Values;

public class MyString implements Addable<MyString>, ValueType<MyString> {

    private String value;

    public MyString(String value) {
        this.value = value;
    }

    public MyString() {
        this.value = "";
    }

    @Override
    public MyString add(MyString other) {
        return new MyString(value + other.value);
    }

    @Override
    public MyString parse(String value) {
        return new MyString(value);
    }

    @Override
    public String stringify() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MyString)) { return false; }

        return ((MyString) o).value.equals(value);
    }
}
