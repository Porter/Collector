package com.porter.collector.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static org.junit.Assert.*;

public class CollectionTest {

    @Test
    public void testJSON() throws Exception {
        Collection collection = ImmutableCollection.builder()
                .id(0L)
                .name("name")
                .userId(3L)
                .build();

        ObjectMapper mapper = Jackson.newObjectMapper();
        String actual = mapper.writeValueAsString(collection);

        assertNotEquals("{}", actual);
    }

}