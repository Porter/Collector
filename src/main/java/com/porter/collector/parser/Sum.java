package com.porter.collector.parser;

import com.porter.collector.values.Addable;
import com.porter.collector.values.ValueType;

import java.util.ArrayList;
import java.util.List;

public class Sum implements Function {

    @Override
    public ValueType exec(Args args) {

        List<Addable> addables = new ArrayList<>();
        for (ValueType type : args.getArgs()) {
            addables.add((Addable) type);
        }

        Addable addable = addables.get(0);
        for (int i = 1; i < addables.size(); i++) {
            addable = (Addable) addable.add(addables.get(i));
        }
        return (ValueType) addable;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Sum;
    }
}
