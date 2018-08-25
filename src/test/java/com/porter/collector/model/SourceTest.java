package com.porter.collector.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static org.junit.Assert.*;

public class SourceTest {

    @Test
    public void testJSON() throws Exception {
        Source source = ImmutableSource.builder()
                .id(0L)
                .name("name")
                .collectionId(1L)
                .userId(2L)
                .type(ValueTypes.FLOAT)
                .build();

        ObjectMapper mapper = Jackson.newObjectMapper();
        String actual = mapper.writeValueAsString(source);

        assertNotEquals("{}", actual);
    }

}