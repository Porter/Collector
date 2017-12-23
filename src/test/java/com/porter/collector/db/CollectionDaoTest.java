package com.porter.collector.db;

import com.porter.collector.errors.CollectionExistsException;
import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.Collection;
import com.porter.collector.model.ImmutableCollection;
import com.porter.collector.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class CollectionDaoTest extends BaseTest {

    private CollectionDao collectionDao;
    private UserDao userDao;

    @Before
    public void getDAOs() {
        collectionDao = getJdbi().onDemand(CollectionDao.class);
        userDao = getJdbi().onDemand(UserDao.class);
    }


    @Test
    public void insert() throws Exception {
        User user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user);
        Collection expected = ImmutableCollection
                .builder()
                .id(collection.id())
                .name("test")
                .user(user)
                .userId(user.id())
                .build();

        Assert.assertEquals(expected, collection);
    }

    @Test(expected = CollectionExistsException.class)
    public void insertDuplicateNames() throws Exception {
        User user = userDao.insert("o@p.com", "name", "pass");

        collectionDao.insert("test", user);
        collectionDao.insert("test", user);
    }

    @Test
    public void insertDuplicateNamesDifferentUsers() throws Exception {
        User user1 = userDao.insert("o@p.com", "name1", "pass");
        User user2 = userDao.insert("i@j.com", "name2", "pass");

        Collection collection1 = collectionDao.insert("test", user1);
        Collection collection2 = collectionDao.insert("test", user2);

        assertEquals(user1, collection1.user());
        assertEquals(user2, collection2.user());
    }

    @Test
    public void findById() throws Exception {
        User user = userDao.insert("i@p.com", "name", "pass");
        Long id = collectionDao.insert("test", user).id();

        Collection collection = collectionDao.findById(id);

        Collection expected = ImmutableCollection
                .builder()
                .id(collection.id())
                .name("test")
                .user(user)
                .userId(user.id())
                .build();

        Assert.assertEquals(expected, collection);
    }


}