package com.porter.collector.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.porter.collector.db.CsvRowDao;
import com.porter.collector.db.SourceDao;
import com.porter.collector.db.ValueDao;
import com.porter.collector.model.*;
import com.porter.collector.model.Values.CustomType;
import com.porter.collector.model.Values.ValueType;
import io.dropwizard.jackson.Jackson;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SourcesController {

    private final SourceDao sourceDao;
    private final ValueDao valueDao;
    private final CsvRowDao csvRowDao;

    public SourcesController(SourceDao sourceDao, ValueDao valueDao, CsvRowDao csvRowDao) {
        this.sourceDao = sourceDao;
        this.valueDao = valueDao;
        this.csvRowDao = csvRowDao;
    }

    public List<Source> getAllFromUser(SimpleUser user) {
        return sourceDao.findAllFromUser(user.id());
    }

    public Source getById(SimpleUser requesting, @PathParam("id") Long id) throws IllegalAccessException {
        Source source = sourceDao.findById(id);
        if (source.userId() != requesting.id()) {
            throw new IllegalAccessException();
        }
        return source;
    }

    public List<Value> getAllValues(SimpleUser requesting, long sourceId) throws IllegalAccessException {
        Source source = sourceDao.findById(sourceId);
        if (source == null) {
            return null;
        }
        if (source.userId() != requesting.id()) {
            throw new IllegalAccessException();
        }

        return valueDao.findBySourceId(sourceId);
    }

    private boolean isValid(Source source, MultivaluedMap<String, String> value) {
        ValueTypes type = source.type();
        if (type == null) {
            CustomType customType = source.customType();
            if (!customType.getTypes().keySet().equals(value.keySet())) { return false; }
            Set<String> keySet = value.keySet();

            for (String key : keySet) {
                if (value.get(key).size() != 1) { return false; }
                ValueTypes valueTypes = customType.getTypes().get(key);

                ValueType t = ValueTypes.getMap().get(valueTypes);
                String keysValue = value.getFirst(key);

                try {
                    value.putSingle(key, t.parse(keysValue).stringify());
                } catch (Exception e) {
                    return false;
                }

            }
            return true;
        }
        else {
            if (!value.containsKey("amount")) {
                return false;
            }
            List<String> values = value.get("amount");
            if (values.size() != 1) {
                return false;
            }
            return ValueTypes.getMap().get(type).isValid(values.get(0));
        }
    }

    private String stringRepr(Source source, MultivaluedMap<String, String> value) {
        if (source.type() == null) {
            ObjectMapper mapper = Jackson.newObjectMapper();
            try {
                Map<String, String> singleMap = new HashMap<>();
                value.forEach((key, val) -> {
                    singleMap.put(key, val.get(0));
                });

                return mapper.writeValueAsString(singleMap);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return "{}";
            }
        } else {
            return value.getFirst("amount");
        }
    }

    public Value addValue(SimpleUser user, long sourceId, MultivaluedMap<String, String> value) {
        Source source = sourceDao.findById(sourceId);
        if (source == null) {
            return null;
        }
        if (user.id() != source.userId()) {
            return null;
        }
        if (!isValid(source, value)) {
            return null;
        }

        String val = stringRepr(source, value);
        return valueDao.insert(val, sourceId);
    }

    public List<Value> getRange(SimpleUser requester, long sourceId, long start, long end) {
        Source source = sourceDao.findById(sourceId);
        if (source == null) {
            return null;
        }
        if (requester.id() != source.userId()) {
            return null;
        }

        return valueDao.getRange(sourceId, start, end);
    }
}
