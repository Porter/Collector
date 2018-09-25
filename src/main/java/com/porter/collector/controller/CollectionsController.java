package com.porter.collector.controller;

import com.porter.collector.db.CollectionDao;
import com.porter.collector.db.CustomTypeDao;
import com.porter.collector.db.SourceDao;
import com.porter.collector.model.*;

import java.util.List;

public class CollectionsController {

    private final CollectionDao collectionDao;
    private final SourceDao sourceDao;
    private final CustomTypeDao customTypeDao;

    public CollectionsController(CollectionDao collectionDao, SourceDao sourceDao, CustomTypeDao customTypeDao) {
        this.collectionDao = collectionDao;
        this.sourceDao = sourceDao;
        this.customTypeDao = customTypeDao;
    }

    public List<Collection> allFromUser(SimpleUser user) {
        return collectionDao.findAllWithUserId(user.id());
    }

    public Collection create(String name, SimpleUser user) {
        return collectionDao.insert(name, user.id());
    }

    public Source addSource(String name, SimpleUser user, long collectionId, int type, long customTypeId,
                            boolean external) throws IllegalAccessException, IllegalArgumentException {

        if (sourceDao.confirmUserOwnsCollection(collectionId, user.id()) == null) {
            throw new IllegalAccessException("You can no longer modify that collection");
        }

        if ((type == -1) == (customTypeId == -1)) {
            throw new IllegalArgumentException("Exactly one of type and customTypeId should be -1");
        }

        ValueTypes valueType = null;
        UsersCustomType customType = null;
        if (type == -1) {
            customType = customTypeDao.findById(customTypeId);
            if (customType.userId() != user.id()) {
                throw new IllegalAccessException("That custom type no longer exists");
            }
        } else {
            try {
                valueType = ValueTypes.values()[type];
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException(e);
            }
        }

        return sourceDao.insert(name, user.id(), collectionId, valueType, customType, external);
    }
}
