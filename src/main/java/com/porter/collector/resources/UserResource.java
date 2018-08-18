package com.porter.collector.resources;


import com.google.common.collect.ImmutableMap;
import com.porter.collector.db.UserDao;
import com.porter.collector.errors.SignUpException;
import com.porter.collector.model.JWTUser;
import com.porter.collector.model.UserWithPassword;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(Urls.USER)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
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
            UserWithPassword user = userDao.insert(email, username, password);
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response auth(@FormParam("name") String name, @FormParam("password") String password) {
        UserWithPassword user = userDao.findByLogin(name);
        if (user == null) {
            return Response.ok(ImmutableMap.of("error", "User not found")).build();
        }
        if (!user.correctPassword(password)) {
            return Response.ok(ImmutableMap.of("error", "Incorrect password")).build();
        }

        return Response.ok(ImmutableMap.of("jwt", JWTUser.toJWT(user))).build();
    }
}
