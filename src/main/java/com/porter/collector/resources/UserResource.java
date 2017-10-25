package com.porter.collector.resources;


import com.porter.collector.db.UsersDao;
import com.porter.collector.model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path(Urls.USER)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UsersDao usersDao;

    public UserResource(UsersDao usersDao) {
        this.usersDao = usersDao;
    }

    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public User create(@FormParam("email") String email,
                           @FormParam("username") String username,
                           @FormParam("password") String password) {
        User user = usersDao.insert(email, username, password);
        return user;
    }

    @POST
    @Path("/login")
    public User auth(@FormParam("login") String login, @FormParam("password") String password) {
        return null;
    }
}
