package com.porter.collector.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IteratorUtil {

    private static class nItr<T> implements Iterator<T> {

        private int n;
        private T val;

        public nItr(int n, T val) {
            this.n = n;
            this.val = val;
        }

        @Override
        public boolean hasNext() {
            return n-- > 0;
        }

        @Override
        public T next() {
            return val;
        }
    }

    public static <T> Iterator<T> nOf(int n, T val) {
        return new nItr<>(n, val);
    }

    public static <T> List<T> listFromItrerator(Iterator<T> iterator) {
        List<T> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }
}
