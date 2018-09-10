package com.porter.collector.parser;

import com.google.common.collect.ImmutableList;
import com.porter.collector.db.CollectionDao;
import com.porter.collector.db.SourceDao;
import com.porter.collector.db.UserDao;
import com.porter.collector.db.ValueDao;
import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.*;
import com.porter.collector.model.Values.MyInteger;
import com.porter.collector.model.Values.MyLong;
import com.porter.collector.model.Values.MyString;
import com.porter.collector.model.Values.ValueType;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class SourceAccessorTest extends BaseTest {

    private SourceAccessor sourceAccessor;
    private UserDao userDao;
    private SourceDao sourceDao;
    private CollectionDao collectionDao;
    private ValueDao valueDao;

    @Before
    public void setUp() {
        sourceDao = getJdbi().onDemand(SourceDao.class);
        valueDao = getJdbi().onDemand(ValueDao.class);
        sourceAccessor = new SourceAccessor(sourceDao, valueDao);

        userDao = getJdbi().onDemand(UserDao.class);
        collectionDao = getJdbi().onDemand(CollectionDao.class);
    }

    @Test
    public void exec() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("collection", user.id());
        Source source = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT);
        Args args = new Args(ImmutableList.of(
                new MyString("source"),
                new MyLong(0)
        ), user);
        ValueType type = sourceAccessor.exec(args);

        assertNull(type);
    }

    @Test
    public void exec2() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("collection", user.id());
        Source source = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT);
        Value value = valueDao.insert("54", source.id());
        Args args = new Args(ImmutableList.of(
                new MyString("source"),
                new MyLong(0)
        ), user);
        ValueType type = sourceAccessor.exec(args);

        assertEquals(new MyInteger(54), type);
    }
}