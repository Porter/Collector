package com.porter.collector.parser;

import com.google.common.collect.ImmutableList;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.values.ValueType;

import java.util.List;

public class Args {

    private List<ValueType> args;
    private SimpleUser requester;

    public Args(ValueType<?>[] args, SimpleUser user) {
        this.args = ImmutableList.copyOf(args);
        this.requester = user;
    }

    public Args(List<ValueType> args, SimpleUser user) {
        this.args = ImmutableList.copyOf(args);
        this.requester = user;
    }

    public int count() {
        return args.size();
    }

    public List<ValueType> getArgs() {
        return args;
    }

    public SimpleUser getRequester() {
        return requester;
    }
}
