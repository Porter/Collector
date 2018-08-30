package com.porter.collector.controller;

import com.porter.collector.db.UserDao;
import com.porter.collector.errors.SignUpException;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.model.UserWithPassword;

public class UsersController {

    private final UserDao userDao;

    public UsersController(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserWithPassword create(String email, String username, String password) throws SignUpException {
        return userDao.insert(email, username, password);
    }

    public SimpleUser getByLogin(String login, String password) {
        UserWithPassword user = userDao.findByLogin(login);
        if (user == null) {
            return null;
        }
        if (!user.correctPassword(password)) {
            return null;
        }

        return user;
    }
}
