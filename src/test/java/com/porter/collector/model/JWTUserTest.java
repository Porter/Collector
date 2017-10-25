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

        JWTUser jwtUser = ImmutableJWTUser.builder()
                .userName("username")
                .id(1L)
                .email("pmh192@gmail.com")
                .build();

        String jwt = JWTUser.toJWT(jwtUser);

        assertEquals("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJKb2UiLCJpZCI6MSwiZW1haWwiOiJ" +
                "wbWgxOTJAZ21haWwuY29tIiwidXNlcm5hbWUiOiJ1c2VybmFtZSJ9.KxzGgElmOnQl-Qn7lEE7" +
                "JgzE8lOiaJMfGRKS4IjZMjnpnQ4QIfgwcqjO_tfi9lHD69eUjSLoNsdwDJZSfBuHPw", jwt);
    }

    @Test
    public void fromJWT() throws Exception {

        JWTUser jwtUser = ImmutableJWTUser.builder()
                .userName("username")
                .id(1L)
                .email("pmh192@gmail.com")
                .build();

        String jwt = JWTUser.toJWT(jwtUser);

        JWTUser actual = JWTUser.fromJWT(jwt);

        assertEquals(actual, jwtUser);
    }

    @Test
    public void fromJWTBadAlgorithm() throws Exception {
        String s = Jwts.builder()
                .setSubject("Joe")
                .claim("id", 1L)
                .claim("email", "mail")
                .claim("username", "name")
                .signWith(SignatureAlgorithm.HS256, MacProvider.generateKey())
                .compact();

        JWTUser jwtUser = JWTUser.fromJWT(s);

        assertNull(jwtUser);
    }

    @Test
    public void fromJWTBadKey() throws Exception {
        String s = Jwts.builder()
                .setSubject("Joe")
                .claim("id", 1L)
                .claim("email", "mail")
                .claim("username", "name")
                .signWith(SignatureAlgorithm.HS512, "wrong key")
                .compact();

        JWTUser jwtUser = JWTUser.fromJWT(s);

        assertNull(jwtUser);
    }
}