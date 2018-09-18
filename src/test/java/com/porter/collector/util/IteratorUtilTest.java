package com.porter.collector.util;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class IteratorUtilTest {

    @Test
    public void nOf() {
        assertFalse(IteratorUtil.nOf(0, null).hasNext());
    }

    @Test
    public void of5() {
        List<Integer> list = ImmutableList.of(0, 0, 0, 0, 0);
        List<Integer> list2 = IteratorUtil.listFromItrerator(IteratorUtil.nOf(5, 0));


        assertEquals(5, list.size());
        assertEquals(list, list2);
    }
}