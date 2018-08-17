package com.porter.collector.db;

import com.porter.collector.errors.*;
import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.UserWithPassword;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserDaoTest extends BaseTest {

    private UserDao userDao;

    @Before
    public void getDao() {
        userDao = getJdbi().onDemand(UserDao.class);
    }

    @Test
    public void insert() throws Exception {
        UserWithPassword user = userDao.insert("pmh192@gmail.com","name", "pass");

        assertEquals(user.email(), "pmh192@gmail.com");
        assertEquals(user.userName(),"name");
    }

    @Test(expected = UserNameExistsException.class)
    public void duplicateUserNames() throws Exception{
        userDao.insert("e1@e.com", "f", "f");
        userDao.insert("e2@e.com", "f", "f");
    }

    @Test(expected = EmailExistsException.class)
    public void duplicateEmails() throws Exception {
        userDao.insert("e@j.com", "ffff", "f");
        userDao.insert("e@j.com", "f", "f");
    }

    @Test
    public void emptyPassword() throws Exception {
        try {
            userDao.insert("username", "", "");
            fail("Should have thrown an error");
        }
        catch (SignUpException e) {
            assertTrue(e.getMessage().contains("Password can not be empty"));
        }
    }

    @Test(expected = InvalidEmailException.class)
    public void invalidEmail() throws Exception{
        userDao.insert("invalid_email", "name", "pass");
    }

    @Test(expected = InvalidUserNameException.class)
    public void emailAsUserName() throws Exception {
        userDao.insert("test@place.com", "email@place.com", "F");
    }

    @Test(expected = InvalidUserNameException.class)
    public void emptyUserName() throws Exception {
        userDao.insert("test@place.com", "", "F");
    }

    @Test
    public void passwordHashed() throws Exception {
        UserWithPassword user = userDao.insert("e@k.com", "i", "test");

        assertTrue(user.correctPassword("test"));
        assertFalse(user.correctPassword("testasdf"));

        assertNotEquals("test", user.hashedPassword());
    }

    @Test
    public void get() throws Exception {
        Long id = userDao.insert("pmh192@gmail.com","name", "pass").id();

        UserWithPassword user = userDao.findById(id);
        assertEquals(id.longValue(), user.id());
        assertEquals("name", user.userName());
        assertEquals("pmh192@gmail.com", user.email());

        assertTrue(user.correctPassword("pass"));
        assertFalse(user.correctPassword("passasd"));

    }

    @Test
    public void findByLogin() throws Exception {
        UserWithPassword user = userDao.insert("pmh192@gmail.com","name", "pass");

        UserWithPassword user0 = userDao.findByLogin("pmh192@gmail.com");
        UserWithPassword user1 = userDao.findByLogin("name");
        UserWithPassword user2 = userDao.findByLogin("somethin");

        assertEquals(user, user0);
        assertEquals(user, user1);
        assertNull(user2);
    }
}