package com.porter.collector.parser;

import com.porter.collector.db.SourceDao;
import com.porter.collector.db.ValueDao;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.model.Source;
import com.porter.collector.model.Value;
import com.porter.collector.model.Values.*;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;


public class SourceAccessor implements Function {

    private final SourceDao sourceDao;
    private final ValueDao valueDao;

    public SourceAccessor(SourceDao sourceDao, ValueDao valueDao) {
        this.sourceDao = sourceDao;
        this.valueDao = valueDao;
    }

    //TODO refactor to somewhere else
    private String getString(ValueType valueType) {
        return ((MyString) valueType).stringify();
    }

    //TODO refactor to somewhere else
    private long getLong(ValueType valueType) {
        try { return ((MyLong) valueType).getValue(); }
        catch (ClassCastException ignored) { }

        return ((MyInteger) valueType).getValue();
    }

    @Override
    public ValueType exec(Args args) {
        if (args.count() == 2) {
            ValueType valueType1 = args.getArgs().get(0);
            ValueType valueType2 = args.getArgs().get(1);
            return singleElement(args.getRequester(), getString(valueType1), getLong(valueType2));
        }
        else if (args.count() == 3) {
            ValueType valueType1 = args.getArgs().get(0);
            ValueType valueType2 = args.getArgs().get(1);
            ValueType valueType3 = args.getArgs().get(2);
            return range(args.getRequester(), getString(valueType1), getLong(valueType2), getLong(valueType3));
        }
        throw new IllegalStateException("Wrong number of args. Must be 2 or 3");
    }

    private ValueType singleElement(SimpleUser requester, String source, long pos) {
        Source usersSource = sourceDao.findByUsersSource(requester.id(), source);
        if (usersSource == null) {
            throw new IllegalStateException("You don't have any sources named: " + source);
        }
        List<Value> values = valueDao.getRange(usersSource.id(), pos, pos);
        if (values.isEmpty()) { return null; }

        try {
            return ValueTypeFactory.getFromValue(usersSource.type(), values.get(0));
        } catch (ParseException e) {
            System.err.print(e);
            return null;
        }
    }

    private MyList range(SimpleUser requester, String source, long start, long end) {
        Source usersSource = sourceDao.findByUsersSource(requester.id(), source);
        if (usersSource == null) {
            throw new IllegalStateException("You don't have any sources named: " + source);
        }
        List<Value> values = valueDao.getRange(usersSource.id(), start, end);
        if (values.isEmpty()) { return null; }

        return new MyList(
                values.stream()
                .map(v -> {
                    try {
                        return ValueTypeFactory.getFromValue(usersSource.type(), v);
                    } catch (ParseException e) {
                        return null;
                    }
                })
                .collect(Collectors.toList())
        );
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SourceAccessor;
    }
}
