package com.porter.collector.values;

import org.junit.Test;

import static org.junit.Assert.*;

public class MyIntegerTest {

    @Test
    public void add() {
        MyInteger a = new MyInteger(4);
        MyInteger b = new MyInteger(5);
        MyInteger c = a.add(b);
        assertEquals(c, new MyInteger(9));
    }

    @Test
    public void parse() {
        MyInteger a = new MyInteger(5);
        MyInteger b = a.parse("5");

        assertEquals(a, b);
    }

    @Test
    public void isValid() {
        MyInteger a = new MyInteger();
        assertFalse(a.isValid("asdf"));
        assertTrue(a.isValid("5"));
    }

    @Test
    public void stringify() {
        String expected = "84";
        assertEquals(expected, new MyInteger(84).stringify());
    }
}