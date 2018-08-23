package com.porter.collector.db;

import com.google.common.collect.ImmutableList;
import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;


public class SourceDaoTest extends BaseTest {

    private CollectionDao collectionDao;
    private UserDao userDao;
    private SourceDao sourceDao;

    @Before
    public void getDAOs() {
        collectionDao = getJdbi().onDemand(CollectionDao.class);
        userDao = getJdbi().onDemand(UserDao.class);
        sourceDao      = getJdbi().onDemand(SourceDao.class);
    }


    @Test
    public void insert_findById() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Long id = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT).id();

        Source expected = ImmutableSource
                .builder()
                .id(id)
                .name("source")
                .collectionId(collection.id())
                .type(ValueTypes.INT)
                .build();

        Assert.assertEquals(expected, sourceDao.findById(id));
    }

    @Test
    public void findAll() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Source source = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT);
        Source source2 = sourceDao.insert("source2", user.id(), collection.id(), ValueTypes.INT);

        List<Source> expected = ImmutableList.of(source, source2);

        Assert.assertEquals(expected, sourceDao.findAll());
    }

    @Test
    public void findAllFromUser() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        UserWithPassword user2 = userDao.insert("a2@g.com", "name2", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Collection collection2 = collectionDao.insert("test", user2.id());
        Source source = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT);
        Source source2 = sourceDao.insert("source2", user2.id(), collection2.id(), ValueTypes.INT);

        List<Source> expected = ImmutableList.of(source);
        List<Source> expected2 = ImmutableList.of(source2);

        Assert.assertEquals(expected, sourceDao.findAllFromUser(user.id()));
        Assert.assertEquals(expected2, sourceDao.findAllFromUser(user2.id()));
    }

    //TODO test bad user_id given
}