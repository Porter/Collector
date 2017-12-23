package com.porter.collector.db;

import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class DeltaDaoTest extends BaseTest {

    private CollectionsDao collectionsDao;
    private UsersDao usersDao;
    private DeltaDao deltaDao;
    private SourceDao sourceDao;

    @Before
    public void getDAOs() {
        collectionsDao = getJdbi().onDemand(CollectionsDao.class);
        usersDao       = getJdbi().onDemand(UsersDao.class);
        deltaDao       = getJdbi().onDemand(DeltaDao.class);
        sourceDao      = getJdbi().onDemand(SourceDao.class);
    }


    @Test
    public void insert_findById() throws Exception {
        User user = usersDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionsDao.insert("test", user);
        Long sourceId = sourceDao.insert("source", collection.id()).id();
        Long id = deltaDao.insert("money", collection.id(), sourceId).id();

        Delta expected = ImmutableDelta
                .builder()
                .id(id)
                .name("money")
                .collectionId(collection.id())
                .sourceId(sourceId)
                .build();

        Assert.assertEquals(expected, deltaDao.findById(id));
    }
}