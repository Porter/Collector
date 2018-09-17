package com.porter.collector.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.porter.collector.db.CustomTypeDao;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.model.UsersCustomType;
import com.porter.collector.model.ValueTypes;
import com.porter.collector.values.CustomType;
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

    public UsersCustomType create(SimpleUser user, String name, List<String> keys, List<Integer> values)
            throws ParseException {
        return customTypeDao.insert(user.id(), name, getCustomType(keys, values));
    }

    private CustomType getCustomType(List<String> keys, List<Integer> values) throws ParseException {
        if (keys.size() != values.size()) {
            throw new IllegalStateException("keys and values must be of same length");
        }

        Map<String, ValueTypes> map = new HashMap<>();
        int amount = ValueTypes.values().length;
        for (int i = 0; i < keys.size(); i++) {
            int type = values.get(i);
            if (type >= amount || type < 0) {
                throw new IllegalStateException("ValueType out of range: " + type);
            }
            map.put(keys.get(i), ValueTypes.values()[type]);
        }

        return new CustomType(map);
    }
}
