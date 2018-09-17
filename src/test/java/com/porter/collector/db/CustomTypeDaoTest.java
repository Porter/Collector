package com.porter.collector.db;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.*;
import static org.junit.Assert.*;

import com.porter.collector.values.CustomType;
import com.porter.collector.values.MyString;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;


public class CustomTypeDaoTest extends BaseTest {

    private UserDao userDao;
    private CustomTypeDao customTypeDao;
    private CollectionDao collectionDao;
    private SourceDao sourceDao;

    @Before
    public void getDAOs() {
        userDao = getJdbi().onDemand(UserDao.class);
        customTypeDao  = getJdbi().onDemand(CustomTypeDao.class);
        collectionDao = getJdbi().onDemand(CollectionDao.class);
        sourceDao = getJdbi().onDemand(SourceDao.class);
    }


    @Test
    public void insert_findById() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        CustomType customType = new CustomType(ImmutableMap.of("string", ValueTypes.STRING));
        Long id = customTypeDao.insert(user.id(), "name", customType).id();

        UsersCustomType expected = ImmutableUsersCustomType
                .builder()
                .id(id)
                .name("name")
                .type(customType)
                .userId(user.id())
                .build();

        assertEquals(expected, customTypeDao.findById(id));
    }

    @Test
    public void findAllFromUser() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        UserWithPassword user2 = userDao.insert("a2@g.com", "name2", "pass");

        CustomType customType = new CustomType(ImmutableMap.of("string", ValueTypes.STRING));
        UsersCustomType type1 = customTypeDao.insert(user.id(), "name", customType);
        UsersCustomType type2 = customTypeDao.insert(user2.id(), "name", customType);
        UsersCustomType type3 = customTypeDao.insert(user2.id(), "name", customType);
        List<UsersCustomType> expected = ImmutableList.of(type1);
        List<UsersCustomType> expected2 = ImmutableList.of(type2, type3);

        assertEquals(expected, customTypeDao.findAllFromUser(user.id()));
        assertEquals(expected2, customTypeDao.findAllFromUser(user2.id()));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test(expected = IllegalStateException.class)
    public void badUserId() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        CustomType type = new CustomType(ImmutableMap.of("string", ValueTypes.STRING));
        customTypeDao.insert(user.id()+ 1, "name", type);
    }

    @Test
    public void findBySourceId() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        CustomType type = new CustomType(ImmutableMap.of("string", ValueTypes.STRING));
        UsersCustomType customType = customTypeDao.insert(user.id(), "name", type);
        Source source = sourceDao.insert("source", user.id(), collection.id(), null, customType, false);

        assertEquals(customType, customTypeDao.findBySourceId(source.id()));

    }
}