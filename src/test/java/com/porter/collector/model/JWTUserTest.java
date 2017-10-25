package com.porter.collector.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JWTUserTest {
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

}