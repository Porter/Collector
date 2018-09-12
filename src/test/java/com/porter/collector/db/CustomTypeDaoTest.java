package com.porter.collector.db;

import com.google.common.collect.ImmutableList;
import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;


public class CustomTypeDaoTest extends BaseTest {

    private UserDao userDao;
    private CustomTypeDao customTypeDao;

    @Before
    public void getDAOs() {
        userDao = getJdbi().onDemand(UserDao.class);
        customTypeDao  = getJdbi().onDemand(CustomTypeDao.class);
    }


    @Test
    public void insert_findById() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Long id = customTypeDao.insert(user.id(), "name", "string").id();

        UsersCustomType expected = ImmutableUsersCustomType
                .builder()
                .id(id)
                .name("name")
                .type("string")
                .userId(user.id())
                .build();

        Assert.assertEquals(expected, customTypeDao.findById(id));
    }

    @Test
    public void findAllFromUser() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        UserWithPassword user2 = userDao.insert("a2@g.com", "name2", "pass");

        UsersCustomType type1 = customTypeDao.insert(user.id(), "name", "string");
        UsersCustomType type2 = customTypeDao.insert(user2.id(), "name", "string");
        UsersCustomType type3 = customTypeDao.insert(user2.id(), "name", "string");
        List<UsersCustomType> expected = ImmutableList.of(type1);
        List<UsersCustomType> expected2 = ImmutableList.of(type2, type3);

        Assert.assertEquals(expected, customTypeDao.findAllFromUser(user.id()));
        Assert.assertEquals(expected2, customTypeDao.findAllFromUser(user2.id()));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test(expected = IllegalStateException.class)
    public void badUserId() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        customTypeDao.insert(user.id()+ 1, "name", "string");
    }

}