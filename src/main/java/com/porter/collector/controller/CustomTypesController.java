package com.porter.collector.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.porter.collector.db.CustomTypeDao;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.model.UsersCustomType;
import io.dropwizard.jackson.Jackson;

import javax.ws.rs.PathParam;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomTypesController {

    private final CustomTypeDao customTypeDao;

    public CustomTypesController(CustomTypeDao customTypeDao) {
        this.customTypeDao = customTypeDao;
    }

    public List<UsersCustomType> getAllFromUser(SimpleUser user) {
        return customTypeDao.findAllFromUser(user.id());
    }

    public UsersCustomType getById(SimpleUser requesting, @PathParam("id") Long id) throws IllegalAccessException {
        UsersCustomType usersCustomType = customTypeDao.findById(id);
        if (usersCustomType.userId() != requesting.id()) {
            throw new IllegalAccessException();
        }
        return usersCustomType;
    }

    public UsersCustomType create(SimpleUser user, String name, List<String> keys, List<String> values)
            throws ParseException {
        return customTypeDao.insert(user.id(), name, getStringRepresentation(keys, values));
    }

    private String getStringRepresentation(List<String> keys, List<String> values) throws ParseException {
        if (keys.size() != values.size()) {
            throw new IllegalStateException("keys and values must be of same length");
        }

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), values.get(i));
        }

        ObjectMapper mapper = Jackson.newObjectMapper();
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ParseException(e.getMessage(), 0);
        }
    }
}
