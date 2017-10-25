package com.porter.collector.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableJWTUser.class)
@JsonDeserialize(as = ImmutableJWTUser.class)
public abstract class JWTUser extends SimpleUser {
    private static final String key = "secret";

    public static JWTUser fromJWT(String jwt) {
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();

        return ImmutableJWTUser.builder()
                .id(claims.get("id", Long.class))
                .email(claims.get("email", String.class))
                .userName(claims.get("username", String.class))
                .build();
    }

    public static String toJWT(JWTUser jwtUser) {
        return Jwts.builder()
                .setSubject("Joe")
                .claim("id", jwtUser.id())
                .claim("email", jwtUser.email())
                .claim("username", jwtUser.userName())
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }
}
