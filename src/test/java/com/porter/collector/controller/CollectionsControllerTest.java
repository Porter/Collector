package com.porter.collector.controller;

import com.porter.collector.db.CollectionDao;
import com.porter.collector.db.CustomTypeDao;
import com.porter.collector.db.SourceDao;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.model.UsersCustomType;
import com.porter.collector.model.ValueTypes;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class CollectionsControllerTest {

    private CollectionDao collectionDao = mock(CollectionDao.class);
    private SourceDao sourceDao = mock(SourceDao.class);
    private CustomTypeDao customTypeDao = mock(CustomTypeDao.class);
    private CollectionsController collectionsController;

    @Before
    public void setUp() {
        collectionsController = new CollectionsController(collectionDao, sourceDao, customTypeDao);
    }

    @Test(expected = IllegalAccessException.class)
    public void addSource() throws Exception {
        long collectionId = 1L;
        SimpleUser user = mock(SimpleUser.class);
        when(user.id()).thenReturn(2L);
        when(sourceDao.confirmUserOwnsCollection(collectionId, user.id())).thenReturn(null);

        collectionsController.addSource("name", user, collectionId, 0, -1, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSourceBothNeg1() throws Exception {
        long collectionId = 1L;
        SimpleUser user = mock(SimpleUser.class);
        when(user.id()).thenReturn(2L);
        when(sourceDao.confirmUserOwnsCollection(collectionId, user.id())).thenReturn(0L);

        collectionsController.addSource("name", user, collectionId, -1, -1, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSourceBothNotNeg1() throws Exception {
        long collectionId = 1L;
        SimpleUser user = mock(SimpleUser.class);
        when(user.id()).thenReturn(2L);
        when(sourceDao.confirmUserOwnsCollection(collectionId, user.id())).thenReturn(0L);

        collectionsController.addSource("name", user, collectionId, 0, 4, false);
    }

    @Test(expected = IllegalAccessException.class)
    public void addSource_badCustomType() throws Exception {
        long collectionId = 1L;
        SimpleUser user = mock(SimpleUser.class);
        when(user.id()).thenReturn(2L);
        when(sourceDao.confirmUserOwnsCollection(collectionId, user.id())).thenReturn(0L);
        UsersCustomType customType = mock(UsersCustomType.class);
        when(customType.id()).thenReturn(3L);
        when(customTypeDao.findById(4L)).thenReturn(customType);

        collectionsController.addSource("name", user, collectionId, -1, 4, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSource_outOfRange() throws Exception {
        long collectionId = 1L;
        SimpleUser user = mock(SimpleUser.class);
        when(user.id()).thenReturn(2L);
        when(sourceDao.confirmUserOwnsCollection(collectionId, user.id())).thenReturn(0L);
        UsersCustomType customType = mock(UsersCustomType.class);
        when(customType.id()).thenReturn(3L);
        when(customTypeDao.findById(4L)).thenReturn(customType);

        collectionsController.addSource("name", user, collectionId, ValueTypes.values().length, -1, false);
    }
}