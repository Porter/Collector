package com.porter.collector.db;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.*;
import com.porter.collector.values.CustomType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;


public class SourceDaoTest extends BaseTest {

    private CollectionDao collectionDao;
    private UserDao userDao;
    private SourceDao sourceDao;
    private CustomTypeDao customTypeDao;

    @Before
    public void getDAOs() {
        collectionDao = getJdbi().onDemand(CollectionDao.class);
        userDao = getJdbi().onDemand(UserDao.class);
        sourceDao      = getJdbi().onDemand(SourceDao.class);
        customTypeDao  = getJdbi().onDemand(CustomTypeDao.class);
    }


    @Test
    public void insert_findById() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Long id = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT, null, false).id();

        Source expected = ImmutableSource
                .builder()
                .id(id)
                .name("source")
                .collectionId(collection.id())
                .userId(user.id())
                .type(ValueTypes.INT)
                .external(false)
                .build();

        assertEquals(expected, sourceDao.findById(id));
    }

    @Test
    public void insert_findByIdCustomType() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        CustomType type = new CustomType(ImmutableMap.of("f", ValueTypes.INT));
        UsersCustomType customType = customTypeDao.insert(user.id(), "name", type);
        Long id = sourceDao.insert("source", user.id(), collection.id(), null, customType, false).id();

        Source expected = ImmutableSource
                .builder()
                .id(id)
                .name("source")
                .collectionId(collection.id())
                .userId(user.id())
                .customType(type)
                .external(false)
                .build();

        assertEquals(expected, sourceDao.findById(id));
    }

    @Test
    public void findAll() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Source source = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT, null, false);
        Source source2 = sourceDao.insert("source2", user.id(), collection.id(), ValueTypes.INT, null, false);

        Set<Source> expected = ImmutableSet.of(source, source2);

        assertEquals(expected, ImmutableSet.copyOf(sourceDao.findAll()));
    }

    @Test
    public void findAllFromUser() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        UserWithPassword user2 = userDao.insert("a2@g.com", "name2", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Collection collection2 = collectionDao.insert("test", user2.id());
        Source source = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT, null, false);
        Source source2 = sourceDao.insert("source2", user2.id(), collection2.id(), ValueTypes.INT, null, false);

        List<Source> expected = ImmutableList.of(source);
        List<Source> expected2 = ImmutableList.of(source2);

        assertEquals(expected, sourceDao.findAllFromUser(user.id()));
        assertEquals(expected2, sourceDao.findAllFromUser(user2.id()));
    }

    @Test
    public void badUserId() throws Exception {
        thrown.expect(IllegalStateException.class);
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        sourceDao.insert("source", user.id() + 1, collection.id(), ValueTypes.INT, null, false);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();


//    TODO wait until https://github.com/jdbi/jdbi/pull/1225 is published
//
//    interface myDao {
//        default void blowUp() throws SQLException { throw new SQLException("boom"); }
//
//        @SqlQuery("SELECT 1")
//        int thisMakesMyDaoASqlObject();
//    }
//
//    // Fails
//    @Test(expected = SQLException.class)
//    public void blowUp() throws Exception {
//        // jdbi has the SqlObjectPlugin installed
//        Jdbi jdbi = getJdbi();
//
//        jdbi.onDemand(myDao.class).blowUp();
//    }
//
//    // Passes, but is tedious
//    @Test
//    public void blowUp2() throws Exception {
//        Jdbi jdbi = getJdbi();
//
//        try {
//            jdbi.onDemand(myDao.class).blowUp();
//            fail("Should have thrown");
//        }
//        catch (RuntimeException e) {
//            assertEquals(SQLException.class, e.getCause().getClass());
//        }
//    }
}