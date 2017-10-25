package com.porter.collector.db;

import com.porter.collector.errors.EmailExistsException;
import com.porter.collector.errors.InvalidEmailException;
import com.porter.collector.errors.InvalidUserNameException;
import com.porter.collector.errors.UserNameExistsException;
import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.User;
import org.junit.Before;
import org.junit.Test;

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
        usersDao.insert("e1@e.com", "f", "f");
        usersDao.insert("e2@e.com", "f", "f");
    }

    @Test(expected = EmailExistsException.class)
    public void duplicateEmails() {
        usersDao.insert("e@j.com", "ffff", "f");
        usersDao.insert("e@j.com", "f", "f");
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

    @Test(expected = InvalidEmailException.class)
    public void invalidEmail() {
        usersDao.insert("invalid_email", "name", "pass");
    }

    @Test(expected = InvalidUserNameException.class)
    public void emailAsUserName(){
        usersDao.insert("test@place.com", "email@place.com", "F");
    }

    @Test(expected = InvalidUserNameException.class)
    public void emptyUserName(){
        usersDao.insert("test@place.com", "", "F");
    }

    @Test
    public void passwordHashed() {
        User user = usersDao.insert("e@k.com", "i", "test");

        assertTrue(user.correctPassword("test"));
        assertFalse(user.correctPassword("testasdf"));

        assertNotEquals("test", user.hashedPassword());
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