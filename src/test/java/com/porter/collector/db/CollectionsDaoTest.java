package com.porter.collector.db;

import com.porter.collector.errors.CollectionExistsException;
import com.porter.collector.helper.BaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class CollectionsDaoTest extends BaseTest {

    private CollectionsDao collectionsDao;
    private UsersDao usersDao;

    @Before
    public void getDAOs() {
        collectionsDao = getJdbi().onDemand(CollectionsDao.class);
        usersDao       = getJdbi().onDemand(UsersDao.class);
    }


    @Test
    public void insert() throws Exception {
        User user = usersDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionsDao.insert("test", user);
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
        User user = usersDao.insert("o@p.com", "name", "pass");

        collectionsDao.insert("test", user);
        collectionsDao.insert("test", user);
    }

    @Test
    public void insertDuplicateNamesDifferentUsers() throws Exception {
        User user1 = usersDao.insert("o@p.com", "name1", "pass");
        User user2 = usersDao.insert("i@j.com", "name2", "pass");

        Collection collection1 = collectionsDao.insert("test", user1);
        Collection collection2 = collectionsDao.insert("test", user2);

        assertEquals(user1, collection1.user());
        assertEquals(user2, collection2.user());
    }

    @Test
    public void findById() throws Exception {
        User user = usersDao.insert("i@p.com", "name", "pass");
        Long id = collectionsDao.insert("test", user).id();

        Collection collection = collectionsDao.findById(id);

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