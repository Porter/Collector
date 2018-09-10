package com.porter.collector.model.Values;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.junit.Assert.*;

public class MyListTest {

    @Test
    public void stringify() {
        MyList list = new MyList(ImmutableList.of(
                new MyFloat(3.4f)
        ));

        assertEquals("[3.4]", list.stringify());
    }
}