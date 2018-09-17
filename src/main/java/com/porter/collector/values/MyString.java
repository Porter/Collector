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
        byte[] one = value.getBytes();
        byte[] two = value.getBytes();
        int m = Math.max(one.length, two.length);
        byte[] three = new byte[m];
        for (int i = 0; i < m; i++) {
            byte b1 = 0, b2 = 0;
            if (i < one.length) { b1 = one[i]; }
            if (i < two.length) { b2 = two[i]; }

            three[i] = (byte) (b1 ^ b2);
        }
        return new MyString(new String(three));
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
