package com.porter.collector.db;

import com.porter.collector.db.ImmutableCollection;
import helper.BaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


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
        User user = usersDao.insert("name", "pass", "port");
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

    @Test
    public void insertDuplicateNames() throws Exception {
        User user = usersDao.insert("name", "pass", "port");

        collectionsDao.insert("test", user);

        try {
            collectionsDao.insert("test", user);
            fail("Should have thrown an error");
        }
        catch (UnableToExecuteStatementException e) {
            assertTrue(e.getMessage().contains("duplicate key value violates unique constraint \"collections_user_id_name_key\""));
        }
    }

    @Test
    public void findById() throws Exception {
        User user = usersDao.insert("name", "pass", "port");
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