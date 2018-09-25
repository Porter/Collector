package com.porter.collector.db;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.porter.collector.csv.CsvInfo;
import com.porter.collector.csv.CsvUpdater;
import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.*;
import com.porter.collector.model.Collection;
import com.porter.collector.values.CustomType;
import com.porter.collector.values.MyInteger;
import com.porter.collector.values.ValueType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;

import static org.junit.Assert.assertEquals;


public class CsvRowDaoTest extends BaseTest {

    private UserDao userDao;
    private CollectionDao collectionDao;
    private SourceDao sourceDao;
    private CsvRowDao csvRowDao;
    private CustomTypeDao customTypeDao;


    @Before
    public void getDAOs() {
        userDao = getJdbi().onDemand(UserDao.class);
        sourceDao      = getJdbi().onDemand(SourceDao.class);
        collectionDao  = getJdbi().onDemand(CollectionDao.class);
        csvRowDao      = getJdbi().onDemand(CsvRowDao.class);
        customTypeDao  = getJdbi().onDemand(CustomTypeDao.class);
    }


    @Test
    public void insert_findById() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        CustomType customType = new CustomType(ImmutableMap.of("i", ValueTypes.INT));
        Map<String, ValueType> map = ImmutableMap.of("i", new MyInteger(4));
        UsersCustomType usersCustomType = customTypeDao.insert(user.id(), "name", customType);
        Source source = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.CUSTOM, usersCustomType, false);
        Long id = csvRowDao.insert(map, 1, true, source.id()).id();


        CommittedCsvRow expected = new CsvRowBuilder()
                .id(id)
                .sourceId(source.id())
                .processed(true)
                .rowNumber(1)
                .row(map)
                .build();

        assertEquals(expected, csvRowDao.findById(id));
    }

    @Test
    public void insert_findByIdList() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        CustomType customType = new CustomType(ImmutableMap.of("i", ValueTypes.INT));
        UsersCustomType usersCustomType = customTypeDao.insert(user.id(), "name", customType);
        Source source = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.CUSTOM, usersCustomType, false);

        List<CsvRow> rows = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            rows.add(ImmutableCsvRow.builder()
                    .row(ImmutableMap.of("i", new MyInteger(4)))
                    .processed(random.nextBoolean())
                    .rowNumber(i)
                    .sourceId(source.id())
                    .build());
        }
        List<CommittedCsvRow> saved = csvRowDao.insert(rows);

        for (int i = 0; i < rows.size(); i++) {
            CsvRow expected = rows.get(i);
            assertEquals(expected, csvRowDao.findById(saved.get(i).id()).csvRow());
        }
    }

    @Test
    public void findAllFromUser() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        CustomType customType = new CustomType(ImmutableMap.of("i", ValueTypes.INT));
        UsersCustomType usersCustomType = customTypeDao.insert(user.id(), "name", customType);
        Map<String, ValueType> map = ImmutableMap.of("i", new MyInteger(4));
        Source source1 = sourceDao.insert("source1", user.id(), collection.id(), ValueTypes.CUSTOM, usersCustomType, false);
        Source source2 = sourceDao.insert("source2", user.id(), collection.id(), ValueTypes.CUSTOM, usersCustomType, false);


        CommittedCsvRow csvRow1 = csvRowDao.insert(map, 1, true, source1.id());
        CommittedCsvRow csvRow2 = csvRowDao.insert(map, 1, true, source2.id());
        CommittedCsvRow csvRow3 = csvRowDao.insert(map, 2, true, source2.id());

        List<CommittedCsvRow> expected = ImmutableList.of(csvRow1);
        List<CommittedCsvRow> expected2 = ImmutableList.of(csvRow2, csvRow3);

        assertEquals(expected, csvRowDao.findAllFromSource(source1.id()));
        assertEquals(expected2, csvRowDao.findAllFromSource(source2.id()));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void badUserId() throws Exception {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("java.lang.IllegalAccessException: You can no longer modify that source");
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Collection collection = collectionDao.insert("test", user.id());
        Source source = sourceDao.insert("source", user.id(), collection.id(), ValueTypes.INT, null, false);
        csvRowDao.insert(ImmutableMap.of(), 0, true, source.id()+1);
    }
}