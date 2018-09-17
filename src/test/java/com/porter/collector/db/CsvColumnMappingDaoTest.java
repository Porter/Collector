package com.porter.collector.db;

import com.google.common.collect.ImmutableMap;
import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Map;


public class CsvColumnMappingDaoTest extends BaseTest {

    private UserDao userDao;
    private SourceDao sourceDao;
    private CollectionDao collectionDao;
    private CsvColumnMappingDao csvColumnMappingDao;

    @Before
    public void getDAOs() {
        userDao = getJdbi().onDemand(UserDao.class);
        sourceDao      = getJdbi().onDemand(SourceDao.class);
        collectionDao  = getJdbi().onDemand(CollectionDao.class);
        csvColumnMappingDao  = getJdbi().onDemand(CsvColumnMappingDao.class);
    }


    @Test
    public void insert() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Source source = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT, null, false);

        Map<String, String>  map = ImmutableMap.of("value", "two");
        CsvColumnMapping csvColumnMapping = csvColumnMappingDao.insert(source.id(), map);

        CsvColumnMapping expected = ImmutableCsvColumnMapping
                .builder()
                .id(csvColumnMapping.id())
                .sourceId(source.id())
                .mapping(map)
                .build();

        Assert.assertEquals(expected, csvColumnMapping);
    }

    @Test
    public void findById() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Source source = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT, null, false);

        Map<String, String>  map = ImmutableMap.of("value", "two");
        CsvColumnMapping csvColumnMapping = csvColumnMappingDao.insert(source.id(), map);

        Assert.assertEquals(csvColumnMapping, csvColumnMappingDao.findById(csvColumnMapping.id()));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void badUserId() throws Exception {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("java.lang.IllegalAccessException: You can no longer access that source");
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Source source = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT, null, false);

        Map<String, String>  map = ImmutableMap.of("value", "two");
        csvColumnMappingDao.insert(source.id() + 1, map);
    }
}