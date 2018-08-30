package com.porter.collector.controller;

import com.porter.collector.db.DeltaDao;
import com.porter.collector.db.SourceDao;
import com.porter.collector.model.Delta;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.model.Source;

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
        return deltaDao.insert(name, Long.parseLong(value), collectionId, sourceId);
    }
}
