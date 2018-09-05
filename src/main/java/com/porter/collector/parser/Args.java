package com.porter.collector.parser;

import com.google.common.collect.ImmutableList;
import com.porter.collector.model.Value;
import com.porter.collector.model.Values.ValueType;

import java.util.List;

public class Args {

    private List<ValueType> args;

    public Args(ValueType<?>[] args) {
        this.args = ImmutableList.copyOf(args);
    }

    public Args(List<ValueType> args) {
        this.args = ImmutableList.copyOf(args);
    }

    public int count() {
        return args.size();
    }

    public List<ValueType> getArgs() {
        return args;
    }
}
