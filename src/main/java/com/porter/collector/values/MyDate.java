package com.porter.collector.values;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Objects;

public class MyDate implements ValueType<MyDate> {

    private LocalDate date;
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");

    public MyDate() {
        this(new LocalDate());
    }

    public MyDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public MyDate parse(String value) throws Exception {
        return new MyDate(formatter.parseLocalDate(value));
    }

    @Override
    public MyDate combine(MyDate other) {
        return zero();
    }

    @Override
    public String stringify() {
        return date.toString(formatter);
    }

    @Override
    public MyDate zero() {
        return new MyDate(new LocalDate(0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MyDate)) return false;
        MyDate other = (MyDate) o;
        return Objects.equals(date, other.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }
}
