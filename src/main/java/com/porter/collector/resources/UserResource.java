package com.porter.collector.resources;


import com.porter.collector.db.UserDao;
import com.porter.collector.errors.SignUpException;
import com.porter.collector.model.JWTUser;
import com.porter.collector.model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path(Urls.USER)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserDao userDao;

    public UserResource(UserDao userDao) {
        this.userDao = userDao;
    }

    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String create(@FormParam("email") String email,
                           @FormParam("username") String username,
                           @FormParam("password") String password) {
        try {
            User user = userDao.insert(email, username, password);
            return JWTUser.toJWT(user);
        }
        catch (SignUpException e) {
            return e.getMessage();
        }
        catch (Exception e) {
            System.out.println(e);
            return "e";
        }
    }

    @POST
    @Path("/login")
    @Produces(MediaType.TEXT_PLAIN)
    public User auth(@FormParam("login") String login, @FormParam("password") String password) {
        return null;
    }
}
