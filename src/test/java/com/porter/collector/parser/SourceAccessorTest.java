package com.porter.collector.parser;

import com.google.common.collect.ImmutableList;
import com.porter.collector.db.CollectionDao;
import com.porter.collector.db.SourceDao;
import com.porter.collector.db.UserDao;
import com.porter.collector.db.ValueDao;
import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.*;
import com.porter.collector.util.ValueValidator;
import com.porter.collector.values.MyInteger;
import com.porter.collector.values.MyLong;
import com.porter.collector.values.MyString;
import com.porter.collector.values.ValueType;
import io.dropwizard.jackson.Jackson;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SourceAccessorTest extends BaseTest {

    private SourceAccessor sourceAccessor;
    private UserDao userDao;
    private SourceDao sourceDao;
    private CollectionDao collectionDao;
    private ValueDao valueDao;

    @Before
    public void setUp() {
        sourceDao = getJdbi().onDemand(SourceDao.class);

        ValueValidator validator = new ValueValidator(Jackson.newObjectMapper());
        valueDao = new ValueDao(getJdbi(), validator, new ValuesMapper());
        sourceAccessor = new SourceAccessor(sourceDao, valueDao);

        userDao = getJdbi().onDemand(UserDao.class);
        collectionDao = getJdbi().onDemand(CollectionDao.class);
    }

    @Test
    public void exec() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("collection", user.id());
        sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT, null, false);
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
        Source source = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT, null, false);
        Value value = valueDao.insert("54", source.id());
        Args args = new Args(ImmutableList.of(
                new MyString("source"),
                new MyLong(0)
        ), user);
        ValueType type = sourceAccessor.exec(args);

        assertEquals(new MyInteger(54), type);
    }
}