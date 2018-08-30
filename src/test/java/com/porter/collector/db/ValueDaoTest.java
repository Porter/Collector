package com.porter.collector.db;

import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.Value;
import com.porter.collector.model.ValueTypes;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValueDaoTest extends BaseTest {

    private ValueDao valueDao;

    @Before
    public void getDao() {
        valueDao = getJdbi().onDemand(ValueDao.class);
    }

    @Test
    public void insert_findById() throws Exception {
        Value value = valueDao.insert("1", ValueTypes.INT);
        Value expected = valueDao.findById(value.id());

        assertEquals(expected, value);
    }
}