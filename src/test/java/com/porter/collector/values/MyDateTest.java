package com.porter.collector.values;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class MyDateTest {

    @Test
    public void parse() throws Exception {
        Calendar calendar = new Calendar.Builder()
                .set(Calendar.YEAR, 2018)
                .set(Calendar.MONTH, Calendar.SEPTEMBER)
                .set(Calendar.DATE, 12)
                .build();
        MyDate parsed = new MyDate().parse("09/12/2018");
        LocalDate date = LocalDate.fromCalendarFields(calendar);
        MyDate expected = new MyDate(date);

        assertEquals(expected, parsed);
    }

    @Test
    public void stringify() throws Exception {
        String expected = "09/12/2018";
        MyDate parsed = new MyDate().parse(expected);

        assertEquals(expected, parsed.stringify());
    }

    @Test
    public void isValid() throws Exception {
        assertTrue(new MyDate().isValid("09/12/2018"));

        assertFalse(new MyDate().isValid("s9/12/2018"));
    }
}