package com.porter.collector.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void deserialization() throws Exception {
        ImmutableUser user = ImmutableUser
                .builder()
                .userName("F")
                .email("F")
                .id(2)
                .hashedPassword("DF")
                .build();

        ObjectMapper mapper = Jackson.newObjectMapper();
        String res = mapper.writeValueAsString(user);
        assertNotEquals(res, "{}");
    }
}
