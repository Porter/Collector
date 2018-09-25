package com.porter.collector.values;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyList implements Addable<MyList>, ValueType<MyList> {

    private List<ValueType> values;

    public MyList(List<ValueType> values) {
        this.values = values;
    }

    public MyList() {
        this.values = new ArrayList<>();
    }

    @Override
    public MyList add(MyList other) {
        List<ValueType> types = new ArrayList<>(values);
        types.addAll(other.values);
        return new MyList(types);
    }

    @Override
    public MyList parse(String value) {
        return new MyList();
    }

    @Override
    public MyList combine(MyList other) {
        return this.add(other);
    }

    @Override
    public String stringify() {
        return values.stream()
                .map(ValueType::stringify)
                .collect(Collectors.toList())
                .toString();
    }

    @Override
    public MyList zero() {
        return new MyList();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) { return true; }
        if (o == null) { return false; }
        if (!(o instanceof MyList)) { return false; }

        return ((MyList) o).values.equals(values);
    }
}
