package com.porter.collector.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.porter.collector.model.ImmutableUser;
import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.ImmutableUserWithPassword;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest extends BaseTest {

    @Test
    public void deserialization() throws Exception {
        ImmutableUserWithPassword user = ImmutableUserWithPassword
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
