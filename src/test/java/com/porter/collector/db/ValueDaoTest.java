package com.porter.collector.db;

import com.google.common.collect.ImmutableList;
import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.*;
import com.porter.collector.util.ValueValidator;
import io.dropwizard.jackson.Jackson;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ValueDaoTest extends BaseTest {

    private ValueDao valueDao;
    private CollectionDao collectionDao;
    private UserDao userDao;
    private SourceDao sourceDao;
    private ValueValidator validator;

    @Before
    public void getDao() {
        validator = new ValueValidator(Jackson.newObjectMapper());

        valueDao = new ValueDao(getJdbi(), validator, new ValuesMapper());
        collectionDao = getJdbi().onDemand(CollectionDao.class);
        userDao = getJdbi().onDemand(UserDao.class);
        sourceDao      = getJdbi().onDemand(SourceDao.class);
    }

    @Test
    public void insert_findById() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Long id = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT, null, false).id();

        Value value = valueDao.insert("1", id);
        Value expected = valueDao.findById(value.id());

        assertEquals(expected, value);
    }

    @Test
    public void insert_findById_Batch() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Long id = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT, null, false).id();

        List<String> values = ImmutableList.of("1", "2", "asdf", "f");
        List<Long> ids = ImmutableList.of(id, id, id, id);
        List<Value> created = valueDao.insert(values, ids);

        for (Value value : created) {
            Value expected = valueDao.findById(value.id());
            assertEquals(expected, value);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void insert_findById_BatchBadArgs() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Long id = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT, null, false).id();

        List<String> values = ImmutableList.of("1", "2", "asdf", "f", "fifth");
        List<Long> ids = ImmutableList.of(id, id, id, id);
        valueDao.insert(values, ids);
    }

    @Test
    public void getRange() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Long id = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT, null, false).id();

        List<Value> values = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            values.add(valueDao.insert("" + i, id));
        }

        List<Value> expected = values.subList(2, 7);

        // start and end are inclusive
        List<Value> actual = valueDao.getRange(id, 2, 6);

        assertEquals(expected, actual);
    }

    @Test
    public void getRangeLimited() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Long id = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT, null, false).id();

        List<Value> values = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            values.add(valueDao.insert("" + i, id));
        }

        List<Value> expected = values.subList(6, 8);

        // start and end are inclusive, outofbounds index treated as max
        List<Value> actual = valueDao.getRange(id, 6, 999);

        assertEquals(expected, actual);
    }

    @Test
    public void getRangeSingle() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Long id = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT, null, false).id();

        List<Value> values = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            values.add(valueDao.insert("" + i, id));
        }

        List<Value> expected = values.subList(6, 7);

        // start and end are inclusive, outofbounds index treated as max
        List<Value> actual = valueDao.getRange(id, 6, 6);

        assertEquals(expected, actual);
        assertEquals(1, actual.size());
    }

    @Test
    public void testNegativeIndex() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Long id = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT, null, false).id();

        List<Value> values = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            values.add(valueDao.insert("" + i, id));
        }

        List<Value> expected = values.subList(1, 6);
        List<Value> actual = valueDao.getRange(id, 1, -2);

        assertEquals(expected, actual);
    }

    @Test
    public void test2NegativeIndex() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Long id = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT, null, false).id();

        List<Value> values = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            values.add(valueDao.insert("" + i, id));
        }

        List<Value> expected = values.subList(5, 6);
        List<Value> actual = valueDao.getRange(id, -3, -2);

        assertEquals(expected, actual);
    }
}