package com.porter.collector.db;

import com.google.common.collect.ImmutableList;
import com.porter.collector.exception.CollectionExistsException;
import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.Collection;
import com.porter.collector.model.ImmutableCollection;
import com.porter.collector.model.UserWithPassword;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Collection expected = ImmutableCollection
                .builder()
                .id(collection.id())
                .name("test")
                .userId(user.id())
                .build();

        Assert.assertEquals(expected, collection);
    }

    @Test(expected = CollectionExistsException.class)
    public void insertDuplicateNames() throws Exception {
        UserWithPassword user = userDao.insert("o@p.com", "name", "pass");

        collectionDao.insert("test", user.id());
        collectionDao.insert("test", user.id());
    }

    @Test
    public void insertDuplicateNamesDifferentUsers() throws Exception {
        UserWithPassword user1 = userDao.insert("o@p.com", "name1", "pass");
        UserWithPassword user2 = userDao.insert("i@j.com", "name2", "pass");

        Collection collection1 = collectionDao.insert("test", user1.id());
        Collection collection2 = collectionDao.insert("test", user2.id());

        assertEquals(user1, userDao.findById(collection1.userId()));
        assertEquals(user2, userDao.findById(collection2.userId()));
    }

    @Test
    public void findById() throws Exception {
        UserWithPassword user = userDao.insert("i@p.com", "name", "pass");
        Long id = collectionDao.insert("test", user.id()).id();

        Collection collection = collectionDao.findById(id);

        Collection expected = ImmutableCollection
                .builder()
                .id(collection.id())
                .name("test")
                .userId(user.id())
                .build();

        Assert.assertEquals(expected, collection);
    }

    @Test
    public void findAll() throws Exception {
        UserWithPassword user = userDao.insert("i@p.com", "name", "pass");
        Long id1 = collectionDao.insert("test0", user.id()).id();
        Long id2 = collectionDao.insert("test1", user.id()).id();
        Long id3 = collectionDao.insert("test2", user.id()).id();

        List<Collection> expected = ImmutableList.of(
                collectionDao.findById(id1),
                collectionDao.findById(id2),
                collectionDao.findById(id3)
        );

        Assert.assertEquals(expected, collectionDao.findAll());
    }

    @Test
    public void findAllWithUserId() throws Exception {
        UserWithPassword user1 = userDao.insert("i1@p.com", "name", "pass");
        UserWithPassword user2 = userDao.insert("i@p.com", "name2", "pass");
        Long id1 = collectionDao.insert("test0", user1.id()).id();
        Long id2 = collectionDao.insert("test1", user1.id()).id();
        Long id3 = collectionDao.insert("test2", user2.id()).id();

        List<Collection> expected1 = ImmutableList.of(
                collectionDao.findById(id1),
                collectionDao.findById(id2)
        );

        List<Collection> expected2 = ImmutableList.of(
                collectionDao.findById(id3)
        );

        Assert.assertEquals(expected1, collectionDao.findAllWithUserId(user1.id()));
        Assert.assertEquals(expected2, collectionDao.findAllWithUserId(user2.id()));
    }
}