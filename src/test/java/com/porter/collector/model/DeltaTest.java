package com.porter.collector.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static org.junit.Assert.*;

public class DeltaTest {

    @Test
    public void testJson() throws Exception {
        Delta delta = ImmutableDelta.builder()
                .id(0L)
                .value("10")
                .collectionId(0L)
                .name("name")
                .sourceId(2L)
                .build();

        ObjectMapper mapper = Jackson.newObjectMapper();
        String actual = mapper.writeValueAsString(delta);

        assertNotEquals("{}", actual);
    }
}