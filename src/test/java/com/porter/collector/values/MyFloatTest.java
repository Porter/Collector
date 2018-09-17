package com.porter.collector.values;

import org.junit.Test;

import static org.junit.Assert.*;

public class MyFloatTest {

    @Test
    public void add() {
        MyFloat a = new MyFloat(4.5f);
        MyFloat b = new MyFloat(5.4f);
        MyFloat c = a.add(b);
        assertEquals(c, new MyFloat(9.9f));
    }

    @Test
    public void parse() {
        MyFloat a = new MyFloat(5);
        MyFloat b = a.parse("5");

        assertEquals(a, b);
    }

    @Test
    public void isValid() {
        MyFloat a = new MyFloat();
        assertFalse(a.isValid("asdf"));
        assertTrue(a.isValid("5"));
    }

    @Test
    public void stringify() {
        String expected = "84.7";
        assertEquals(expected, new MyFloat(84.7f).stringify());
    }
}