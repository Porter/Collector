package com.porter.collector.util;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class IteratorUtilTest {

    @Test(expected = UnsupportedOperationException.class)
    public void instantiate() throws Exception {
        new IteratorUtil();
    }

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