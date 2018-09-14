package com.porter.collector.model.Values;

import org.junit.Test;

import static org.junit.Assert.*;

public class MoneyTest {

    @Test
    public void parse() {
        Money expected = new Money(675);
        Money parsed = expected.parse("$6.75");

        assertEquals(expected, parsed);
    }

    @Test
    public void parse2() {
        Money expected = new Money(-675);
        Money parsed = expected.parse("($6.75)");

        assertEquals(expected, parsed);
    }

    @Test
    public void parser3() {
        Money expected = new Money(-675);
        Money parsed = expected.parse("-$6.75");

        assertEquals(expected, parsed);
    }

    @Test
    public void parser4() {
        Money expected = new Money(-675);
        Money parsed = expected.parse("$-6.75");

        assertEquals(expected, parsed);
    }

    @Test
    public void parse5() {
        Money expected = new Money(300);
        Money parsed = expected.parse("3");

        assertEquals(expected, parsed);
    }

    @Test
    public void stringify() {
        Money money = new Money(432);

        String actual = money.stringify();
        String expected = "$4.32";

        assertEquals(expected, actual);
    }

    @Test
    public void stringify2() {
        Money money = new Money(-522);

        String actual = money.stringify();
        String expected = "($5.22)";

        assertEquals(expected, actual);
    }

    @Test
    public void isValid() {
        Money money = new Money();

        assertTrue(money.isValid("12"));
    }
}