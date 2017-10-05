package com.porter.collector.db;

import helper.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

import static org.junit.Assert.*;

public class UsersDaoTest extends BaseTest {

    private UsersDao usersDao;

    @Before
    public void getDao() {
        usersDao = getJdbi().onDemand(UsersDao.class);
    }

    @Test
    public void insert() throws Exception {
        User user = usersDao.insert("name", "pass", "port");

        assertEquals(user.userName(),"name");
        assertEquals(user.name(), "port");
    }

    @Test()
    public void duplicateUserNames() {
        usersDao.insert("username", "f", "f");

        try {
            usersDao.insert("username", "f", "f");
        }
        catch (UnableToExecuteStatementException e) {
            assertTrue(e.getMessage().contains("duplicate key value violates unique constraint \"users_username_key\""));
        }
    }

    @Test
    public void emptyPassword() {
        try {
            usersDao.insert("username", "", "");
            fail("Should have thrown an error");
        }
        catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Password can not be empty"));
        }
    }

    @Test
    public void get() throws Exception {
        Long id = usersDao.insert("name", "pass", "port").id();

        User user = usersDao.findById(id);
        assertEquals(id.longValue(), user.id());
        assertEquals("name", user.userName());
        assertEquals("port", user.name());

        assertTrue(user.correctPassword("pass"));
        assertFalse(user.correctPassword("passasd"));

    }

}