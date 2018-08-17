package com.porter.collector.db;

import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


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
        Long id = sourceDao.insert("source", collection.id()).id();

        Source expected = ImmutableSource
                .builder()
                .id(id)
                .name("source")
                .collectionId(collection.id())
                .build();

        Assert.assertEquals(expected, sourceDao.findById(id));
    }
}