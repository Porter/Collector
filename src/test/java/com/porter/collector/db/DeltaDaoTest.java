package com.porter.collector.db;

import com.google.common.collect.ImmutableList;
import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;


public class DeltaDaoTest extends BaseTest {

    private CollectionDao collectionDao;
    private UserDao userDao;
    private DeltaDao deltaDao;
    private SourceDao sourceDao;
    private CategoryDao categoryDao;

    @Before
    public void getDAOs() {
        collectionDao = getJdbi().onDemand(CollectionDao.class);
        userDao = getJdbi().onDemand(UserDao.class);
        deltaDao       = getJdbi().onDemand(DeltaDao.class);
        sourceDao      = getJdbi().onDemand(SourceDao.class);
        categoryDao    = getJdbi().onDemand(CategoryDao.class);
    }


    @Test
    public void insert_findById_NoCategory() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Long sourceId = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT).id();
        Long id = deltaDao.insert("money", 10L, collection.id(), sourceId).id();

        Delta expected = ImmutableDelta
                .builder()
                .id(id)
                .name("money")
                .amount(10L)
                .collectionId(collection.id())
                .sourceId(sourceId)
                .build();

        Assert.assertEquals(expected, deltaDao.findById(id));
    }

    @Test
    public void insert_findById_WithCategory() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Long sourceId = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT).id();
        Long categoryId = categoryDao.insert("category", collection.id()).id();
        Long id = deltaDao.insert("money", 10L, collection.id(), sourceId, categoryId).id();

        Delta expected = ImmutableDelta
                .builder()
                .id(id)
                .name("money")
                .amount(10L)
                .collectionId(collection.id())
                .sourceId(sourceId)
                .categoryId(categoryId)
                .build();

        Assert.assertEquals(expected, deltaDao.findById(id));
    }

    @Test
    public void findBySourceId() throws Exception{
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Long sourceId = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT).id();
        Long sourceId2 = sourceDao.insert("source2", user.id(), collection.id(), ValueTypes.INT).id();
        Delta one = deltaDao.insert("money", 10L, collection.id(), sourceId, null);
        Delta two = deltaDao.insert("money", 1L, collection.id(), sourceId, null);
        Delta other = deltaDao.insert("money", 5L, collection.id(), sourceId2, null);

        List<Delta> actual = deltaDao.findBySourceId(sourceId);
        List<Delta> expected = ImmutableList.of(one, two);

        Assert.assertEquals(expected, actual);
    }
}