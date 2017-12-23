package com.porter.collector.db;

import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class SourceDaoTest extends BaseTest {

    private CollectionsDao collectionsDao;
    private UsersDao usersDao;
    private SourceDao sourceDao;

    @Before
    public void getDAOs() {
        collectionsDao = getJdbi().onDemand(CollectionsDao.class);
        usersDao       = getJdbi().onDemand(UsersDao.class);
        sourceDao      = getJdbi().onDemand(SourceDao.class);
    }


    @Test
    public void insert_findById() throws Exception {
        User user = usersDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionsDao.insert("test", user);
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