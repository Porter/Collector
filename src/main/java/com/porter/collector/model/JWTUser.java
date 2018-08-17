package com.porter.collector.model;

import io.jsonwebtoken.*;

import javax.annotation.Nullable;

public abstract class JWTUser extends SimpleUser {
    private static final String key = "secret";
    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

    @Nullable
    public static SimpleUser fromJWT(String jwt) {
       Jws<Claims> parsed;
       try {
           parsed = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt);

           boolean correctAlgorithm = parsed.getHeader().getAlgorithm().equals(signatureAlgorithm.getValue());
           if (!correctAlgorithm) {
               return null;
           }
       }
       catch (SignatureException e) {
           return null;
       }

       Claims claims = parsed.getBody();

       return ImmutableUserWithoutPassword.builder()
               .id(claims.get("id", Long.class))
               .email(claims.get("email", String.class))
               .userName(claims.get("username", String.class))
               .build();
    }

    public static String toJWT(SimpleUser user) {
        return Jwts.builder()
                .claim("id", user.id())
                .claim("email", user.email())
                .claim("username", user.userName())
                .signWith(signatureAlgorithm, key)
                .compact();
    }
}
