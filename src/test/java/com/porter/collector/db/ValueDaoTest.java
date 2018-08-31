package com.porter.collector.db;

import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.Collection;
import com.porter.collector.model.UserWithPassword;
import com.porter.collector.model.Value;
import com.porter.collector.model.ValueTypes;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValueDaoTest extends BaseTest {

    private ValueDao valueDao;
    private CollectionDao collectionDao;
    private UserDao userDao;
    private SourceDao sourceDao;

    @Before
    public void getDao() {
        valueDao = getJdbi().onDemand(ValueDao.class);
        collectionDao = getJdbi().onDemand(CollectionDao.class);
        userDao = getJdbi().onDemand(UserDao.class);
        sourceDao      = getJdbi().onDemand(SourceDao.class);
    }

    @Test
    public void insert_findById() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Long id = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT).id();

        Value value = valueDao.insert("1", id);
        Value expected = valueDao.findById(value.id());

        assertEquals(expected, value);
    }
}