package com.porter.collector.controller;

import com.porter.collector.db.DeltaDao;
import com.porter.collector.db.SourceDao;
import com.porter.collector.model.*;
import com.porter.collector.model.Values.ValueType;

import javax.ws.rs.PathParam;
import java.util.List;

public class SourcesController {

    private final SourceDao sourceDao;
    private final DeltaDao deltaDao;

    public SourcesController(SourceDao sourceDao, DeltaDao deltaDao) {
        this.sourceDao = sourceDao;
        this.deltaDao = deltaDao;
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

    public List<Delta> getAllDeltas(SimpleUser requesting, long sourceId) throws IllegalAccessException {
        Source source = sourceDao.findById(sourceId);
        if (source == null) {
            return null;
        }
        if (source.userId() != requesting.id()) {
            throw new IllegalAccessException();
        }

        return deltaDao.findBySourceId(sourceId);
    }

    public Delta addDelta(SimpleUser user, long sourceId, long collectionId, String name, String value) {
        Source source = sourceDao.findById(sourceId);
        if (source == null) {
            return null;
        }

        ValueTypes type = source.type();
        if (!ValueTypes.map.get(type).isValid(value)) {
            return null;
        }

        return deltaDao.insert(name, value, collectionId, sourceId);
    }
}
