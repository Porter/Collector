package com.porter.collector.controller;

import com.porter.collector.db.CollectionDao;
import com.porter.collector.db.SourceDao;
import com.porter.collector.model.Collection;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.model.Source;
import com.porter.collector.model.ValueTypes;

import java.util.List;

public class CollectionsController {

    private final CollectionDao collectionDao;
    private final SourceDao sourceDao;

    public CollectionsController(CollectionDao collectionDao, SourceDao sourceDao) {
        this.collectionDao = collectionDao;
        this.sourceDao = sourceDao;
    }

    public List<Collection> allFromUser(SimpleUser user) {
        return collectionDao.findAllWithUserId(user.id());
    }

    public Collection create(String name, SimpleUser user) {
        return collectionDao.insert(name, user.id());
    }

    public Source addSource(String name, SimpleUser user, long collectionId, ValueTypes type)
            throws IllegalAccessException {
        return sourceDao.insert(name, user.id(), collectionId, type);
    }
}
