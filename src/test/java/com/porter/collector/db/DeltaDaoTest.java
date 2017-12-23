package com.porter.collector.db;

import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.Collection;
import com.porter.collector.model.Delta;
import com.porter.collector.model.ImmutableDelta;
import com.porter.collector.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class DeltaDaoTest extends BaseTest {

    private CollectionsDao collectionsDao;
    private UsersDao usersDao;
    private DeltaDao deltaDao;

    @Before
    public void getDAOs() {
        collectionsDao = getJdbi().onDemand(CollectionsDao.class);
        usersDao       = getJdbi().onDemand(UsersDao.class);
        deltaDao       = getJdbi().onDemand(DeltaDao.class);
    }


    @Test
    public void insert_findById() throws Exception {
        User user = usersDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionsDao.insert("test", user);
        Long id = deltaDao.insert("money", collection.id()).id();

        Delta expected = ImmutableDelta
                .builder()
                .id(id)
                .name("money")
                .collectionId(collection.id())
                .build();

        Assert.assertEquals(expected, deltaDao.findById(id));
    }
}