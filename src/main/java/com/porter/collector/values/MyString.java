package com.porter.collector.values;

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
    public MyString combine(MyString other) {
        int size = Math.max(value.length(), other.value.length());
        StringBuilder sb = new StringBuilder(size);

        for (int i = 0; i < size; i++) {
            char c1 = 0, c2 = 0;
            if (i < value.length()) { c1 = value.charAt(i); }
            if (i < other.value.length()) { c2 = other.value.charAt(i); }
            sb.append((char) Math.max(c1, c2));
        }

        return new MyString(sb.toString());
    }

    @Override
    public String stringify() {
        return value;
    }

    @Override
    public MyString zero() {
        return new MyString("");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MyString)) { return false; }

        return ((MyString) o).value.equals(value);
    }

    @Override
    public String toString() {
        return "MyString{" +
                "value='" + value + '\'' +
                '}';
    }
}
