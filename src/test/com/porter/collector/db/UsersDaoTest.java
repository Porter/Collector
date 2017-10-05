package com.porter.collector.db;

import com.porter.collector.errors.EmailExistsException;
import com.porter.collector.errors.UserNameExistsException;
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
        User user = usersDao.insert("pmh192@gmail.com","name", "pass");

        assertEquals(user.email(), "pmh192@gmail.com");
        assertEquals(user.userName(),"name");
    }

    @Test(expected = UserNameExistsException.class)
    public void duplicateUserNames() {
        usersDao.insert("mail2", "f", "f");
        usersDao.insert("mail", "f", "f");
    }

    @Test(expected = EmailExistsException.class)
    public void duplicateEmails() {
        usersDao.insert("mail", "ffff", "f");
        usersDao.insert("mail", "f", "f");
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
        Long id = usersDao.insert("pmh192@gmail.com","name", "pass").id();

        User user = usersDao.findById(id);
        assertEquals(id.longValue(), user.id());
        assertEquals("name", user.userName());
        assertEquals("pmh192@gmail.com", user.email());

        assertTrue(user.correctPassword("pass"));
        assertFalse(user.correctPassword("passasd"));

    }

}