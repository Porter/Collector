package com.porter.collector.model;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JWTUserTest {

    @Test
    public void toJWT() throws Exception {

        UserWithoutPassword user = ImmutableUserWithoutPassword.builder()
                .userName("username")
                .id(1L)
                .email("pmh192@gmail.com")
                .build();

        String jwt = JWTUser.toJWT(user);

        assertEquals("eyJhbGciOiJIUzUxMiJ9.eyJpZCI6MSwiZW1haWwiOiJwbW" +
                "gxOTJAZ21haWwuY29tIiwidXNlcm5hbWUiOiJ1c2VybmFtZSJ9.UN" +
                "gP_QIygQ9i5gopK0VJVSGHNwbDsUQxZ5oBfpNEjH-V8lgTdheytVHbvGOYj6L" +
                "4135448pUPD6_I9XJqjuKYg", jwt);
    }

    @Test
    public void fromJWT() throws Exception {

        UserWithoutPassword user = ImmutableUserWithoutPassword.builder()
                .userName("username")
                .id(1L)
                .email("pmh192@gmail.com")
                .build();

        String jwt = JWTUser.toJWT(user);

        SimpleUser actual = JWTUser.fromJWT(jwt);

        assertEquals(actual, user);
    }

    @Test
    public void fromJWTBadAlgorithm() throws Exception {
        String s = Jwts.builder()
                .claim("id", 1L)
                .claim("email", "mail")
                .claim("username", "name")
                .signWith(SignatureAlgorithm.HS256, MacProvider.generateKey())
                .compact();

        SimpleUser jwtUser = JWTUser.fromJWT(s);

        assertNull(jwtUser);
    }

    @Test
    public void fromJWTBadKey() throws Exception {
        String s = Jwts.builder()
                .claim("id", 1L)
                .claim("email", "mail")
                .claim("username", "name")
                .signWith(SignatureAlgorithm.HS512, "wrong key")
                .compact();

        SimpleUser jwtUser = JWTUser.fromJWT(s);

        assertNull(jwtUser);
    }
}