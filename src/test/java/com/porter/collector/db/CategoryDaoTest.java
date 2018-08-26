package com.porter.collector.db;

import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class CategoryDaoTest extends BaseTest {

    private CollectionDao collectionDao;
    private UserDao userDao;
    private CategoryDao categoryDao;

    @Before
    public void getDAOs() {
        collectionDao = getJdbi().onDemand(CollectionDao.class);
        userDao = getJdbi().onDemand(UserDao.class);
        categoryDao = getJdbi().onDemand(CategoryDao.class);
    }


    @Test
    public void insert_findById() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Category category = categoryDao.insert("category", collection.id());

        Category expected = ImmutableCategory
                .builder()
                .id(category.id())
                .name("category")
                .collectionId(collection.id())
                .build();

        Assert.assertEquals(expected, categoryDao.findById(category.id()));
    }
}