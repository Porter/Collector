package com.porter.collector.values;

public class Money implements Addable<Money>, ValueType<Money> {

    private int value;

    public Money(int value) {
        this.value = value;
    }

    public Money() {
        this.value = 0;
    }

    public int getValue() {
        return value;
    }

    @Override
    public Money add(Money other) {
        return new Money(value + other.value);
    }

    @Override
    public Money parse(String value) {
        if (value.isEmpty()) { return new Money(); }

        int multiplier = 100;
        if (value.startsWith("(") && value.endsWith(")")) {
            value = value.substring(1, value.length()-1);
            multiplier *= -1;
        }

        if (value.startsWith("-")) {
            value = value.substring(1);
            multiplier *= -1;
        }

        return _parse(value, multiplier);
    }

    @Override
    public Money combine(Money other) {
        return this.add(other);
    }

    private Money _parse(String value, int multiplier) {
        if (value.startsWith("$")) { value = value.substring(1); }
        if (value.contains(".")) {
            return new Money((int) (multiplier * Float.parseFloat(value)));
        }
        return new Money(multiplier * Integer.parseInt(value));
    }

    @Override
    public String stringify() {
        boolean negative = value < 0;
        value = Math.abs(value);
        String amount = String.format("$%d.%02d", value/100, value%100);

        if (negative) {
            amount = "(" + amount + ")";
        }
        return amount;
    }

    @Override
    public Money zero() {
        return new Money(0);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Money)) { return false; }

        return ((Money) o).value == value;
    }

    @Override
    public String toString() {
        return stringify();
    }
}
