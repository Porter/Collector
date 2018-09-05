package com.porter.collector.parser;

import com.porter.collector.model.Values.ValueType;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionTree {

    private List<FunctionTree> nodes;
    private Function function;

    public FunctionTree(Function func, List<FunctionTree> args) {
        this.function = func;
        this.nodes = args;
    }

    @Override
    public String toString() {
        return function.getClass().toString() + ": " + nodes.toString();
    }

    public ValueType execute() {
        List<ValueType> args = nodes.stream()
                .map(FunctionTree::execute)
                .collect(Collectors.toList());


        return function.exec(new Args(args));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) { return false; }
        if (!(o instanceof FunctionTree)) { return false; }
        FunctionTree other = (FunctionTree) o;
        if (!function.equals(other.function)) { return false; }
        return nodes.equals(other.nodes);
    }
}
