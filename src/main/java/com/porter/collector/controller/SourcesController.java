package com.porter.collector.controller;

import com.porter.collector.db.SourceDao;
import com.porter.collector.db.ValueDao;
import com.porter.collector.model.*;

import javax.ws.rs.PathParam;
import java.util.List;

public class SourcesController {

    private final SourceDao sourceDao;
    private final ValueDao valueDao;

    public SourcesController(SourceDao sourceDao, ValueDao valueDao) {
        this.sourceDao = sourceDao;
        this.valueDao = valueDao;
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

    public Value addValue(SimpleUser user, long sourceId, String value) {
        Source source = sourceDao.findById(sourceId);
        if (source == null) {
            return null;
        }
        if (user.id() != source.userId()) {
            return null;
        }

        ValueTypes type = source.type();
        if (!ValueTypes.map.get(type).isValid(value)) {
            return null;
        }

        return valueDao.insert(value, sourceId);
    }
}
