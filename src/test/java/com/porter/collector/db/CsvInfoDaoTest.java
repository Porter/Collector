package com.porter.collector.db;

import com.google.common.collect.ImmutableMap;
import com.porter.collector.csv.CsvInfo;
import com.porter.collector.csv.ImmutableCsvInfo;
import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.*;
import com.porter.collector.values.CustomType;
import com.porter.collector.values.MyString;
import com.porter.collector.values.ValueType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Map;

import static org.junit.Assert.assertEquals;


public class CsvInfoDaoTest extends BaseTest {

    private UserDao userDao;
    private SourceDao sourceDao;
    private CollectionDao collectionDao;
    private CsvInfoDao csvInfoDao;
    private CustomTypeDao customTypeDao;

    @Before
    public void getDAOs() {
        userDao = getJdbi().onDemand(UserDao.class);
        sourceDao      = getJdbi().onDemand(SourceDao.class);
        collectionDao  = getJdbi().onDemand(CollectionDao.class);
        csvInfoDao     = getJdbi().onDemand(CsvInfoDao.class);
        customTypeDao  = getJdbi().onDemand(CustomTypeDao.class);
    }


    @Test
    public void insert() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        UsersCustomType customType = customTypeDao.insert(user.id(), "myType", new CustomType(
                ImmutableMap.of("value", ValueTypes.STRING)
        ));
        Source source = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.CUSTOM, customType, false);

        Map<String, String>  map = ImmutableMap.of("value", "str");
        Map<String, ValueType> info = ImmutableMap.of("value", new MyString("str"));
        CsvInfo inserted = csvInfoDao.insert(source.id(), map, info, 1);

        CsvInfo expected = ImmutableCsvInfo.builder()
                .sourceId(source.id())
                .info(info)
                .sourceId(source.id())
                .rowCount(1)
                .columnMapping(map)
                .build();

        assertEquals(expected, inserted);
    }

    @Test
    public void findBySourceId() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        UsersCustomType customType = customTypeDao.insert(user.id(), "myType", new CustomType(
                ImmutableMap.of("value", ValueTypes.STRING)
        ));
        Source source = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.CUSTOM, customType, false);

        Map<String, String>  map = ImmutableMap.of("value", "str");
        Map<String, ValueType> info = ImmutableMap.of("value", new MyString("str"));
        CsvInfo inserted = csvInfoDao.insert(source.id(), map, info, 1);

        assertEquals(inserted, csvInfoDao.findBySourceId(inserted.sourceId()));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void badUserId() throws Exception {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("java.lang.IllegalAccessException: You can no longer access that source");
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        UsersCustomType customType = customTypeDao.insert(user.id(), "myType", new CustomType(
                ImmutableMap.of("value", ValueTypes.STRING)
        ));
        Source source = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.CUSTOM, customType, false);

        Map<String, String>  map = ImmutableMap.of("value", "str");
        Map<String, ValueType> info = ImmutableMap.of("value", new MyString("str"));
        csvInfoDao.insert(source.id() + 1, map, info, 1);
    }

    @Test
    public void updateCsvInfo() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        UsersCustomType customType = customTypeDao.insert(user.id(), "myType", new CustomType(
                ImmutableMap.of("value", ValueTypes.STRING)
        ));
        Source source = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.CUSTOM, customType, false);

        Map<String, String>  map = ImmutableMap.of("value", "str");
        Map<String, ValueType> info = ImmutableMap.of("value", new MyString("str"));
        CsvInfo inserted = csvInfoDao.insert(source.id(), map, info, 1);

        Map<String, ValueType> newInfo = ImmutableMap.of("value", new MyString("str part 2"));
        csvInfoDao.updateCsvInfo(inserted.sourceId(), newInfo, 2);

        CsvInfo expected = ImmutableCsvInfo.builder()
                .info(newInfo)
                .rowCount(2)
                .sourceId(source.id())
                .columnMapping(inserted.columnMapping())
                .build();

        assertEquals(expected, csvInfoDao.findBySourceId(source.id()));
    }
}