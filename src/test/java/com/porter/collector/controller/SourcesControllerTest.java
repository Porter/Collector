package com.porter.collector.controller;

import com.google.common.collect.ImmutableMap;
import com.porter.collector.csv.CsvInfo;
import com.porter.collector.csv.CsvParserWrapper;
import com.porter.collector.csv.CsvUpdater;
import com.porter.collector.db.CsvInfoDao;
import com.porter.collector.db.SourceDao;
import com.porter.collector.db.ValueDao;
import com.porter.collector.exception.InconsistentDataException;
import com.porter.collector.model.CsvRow;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.model.Source;
import com.porter.collector.model.Value;
import com.porter.collector.util.ValueValidator;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MultivaluedMap;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SourcesControllerTest {

    private CsvParserWrapper wrapper;
    private SourcesController controller;
    private SourceDao sourceDao;
    private ValueDao valueDao;
    private ValueValidator validator;
    private CsvUpdater updater;
    private CsvInfoDao csvInfoDao;

    @Before
    public void setUp() {
        wrapper = mock(CsvParserWrapper.class);
        sourceDao = mock(SourceDao.class);
        valueDao = mock(ValueDao.class);
        csvInfoDao = mock(CsvInfoDao.class);
        validator = mock(ValueValidator.class);
        updater = mock(CsvUpdater.class);
        controller = new SourcesController(sourceDao, valueDao, null, csvInfoDao,
                null, wrapper, validator, updater);
    }

    @Test(expected = NotFoundException.class)
    public void getByIdBadSourceId() throws Exception {
        SimpleUser user = mock(SimpleUser.class);
        when(sourceDao.findById(5L)).thenReturn(null);
        controller.getById(user, 5L);
    }

    @Test(expected = IllegalAccessException.class)
    public void getByIdBadUserId() throws Exception {
        SimpleUser user = mock(SimpleUser.class);
        Source source = mock(Source.class);
        when(source.userId()).thenReturn(4L);
        when(sourceDao.findById(5L)).thenReturn(source);
        when(user.id()).thenReturn(3L);
        controller.getById(user, 5L);
    }

    @Test
    public void getById() throws Exception {
        SimpleUser user = mock(SimpleUser.class);
        Source source = mock(Source.class);
        when(source.userId()).thenReturn(4L);
        when(sourceDao.findById(5L)).thenReturn(source);
        when(user.id()).thenReturn(4L);
        assertEquals(source, controller.getById(user, 5L));
    }

    @Test(expected = NotFoundException.class)
    public void getAllValuesBadSourceId() throws Exception {
        SimpleUser user = mock(SimpleUser.class);
        when(sourceDao.findById(5L)).thenReturn(null);
        controller.getAllValues(user, 5L);
    }

    @Test(expected = IllegalAccessException.class)
    public void getAllValuesBadUserId() throws Exception {
        SimpleUser user = mock(SimpleUser.class);
        Source source = mock(Source.class);
        when(source.userId()).thenReturn(4L);
        when(sourceDao.findById(5L)).thenReturn(source);
        when(user.id()).thenReturn(3L);
        controller.getAllValues(user, 5L);
    }

    @Test
    public void getAllValues() throws Exception {
        SimpleUser user = mock(SimpleUser.class);
        Source source = mock(Source.class);
        when(source.userId()).thenReturn(4L);
        when(source.id()).thenReturn(5L);
        when(sourceDao.findById(5L)).thenReturn(source);
        when(user.id()).thenReturn(4L);

        List<Value> values = mock(List.class);
        when(valueDao.findBySourceId(5L)).thenReturn(values);
        assertEquals(values, controller.getAllValues(user, 5L));
    }



    @Test(expected = NotFoundException.class)
    public void addValueNotFound() throws Exception {
        when(sourceDao.findById(99L)).thenReturn(null);
        SimpleUser user = mock(SimpleUser.class);
        MultivaluedMap<String, String> values = mock(MultivaluedMap.class);
        controller.addValue(user, 99L, values);
    }

    @Test(expected = IllegalAccessException.class)
    public void addValueWrongUser() throws Exception {
        Source source = mock(Source.class);
        when(source.userId()).thenReturn(101L);
        when(sourceDao.findById(99L)).thenReturn(source);

        SimpleUser user = mock(SimpleUser.class);
        when(user.id()).thenReturn(102L);

        MultivaluedMap<String, String> values = mock(MultivaluedMap.class);

        controller.addValue(user, 99L, values);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addInvalidValue() throws Exception {
        Source source = mock(Source.class);
        when(source.userId()).thenReturn(101L);
        when(sourceDao.findById(99L)).thenReturn(source);

        SimpleUser user = mock(SimpleUser.class);
        when(user.id()).thenReturn(101L);

        MultivaluedMap<String, String> values = mock(MultivaluedMap.class);
        Map<String, String> map = mock(MultivaluedMap.class);

        when(validator.isValid(source, map)).thenReturn(false);

        controller.addValue(user, 99L, values);
    }

    @Test
    public void addValue() throws Exception {
        Source source = mock(Source.class);
        when(source.userId()).thenReturn(101L);
        when(sourceDao.findById(99L)).thenReturn(source);

        SimpleUser user = mock(SimpleUser.class);
        when(user.id()).thenReturn(101L);

        MultivaluedMap<String, String> values = mock(MultivaluedMap.class);
        Map<String, String> map = mock(MultivaluedMap.class);

        when(validator.fromMultivaluedMap(any())).thenReturn(map);
        when(validator.stringRepr(source, map)).thenReturn("stringRepr");
        when(validator.isValid(source, map)).thenReturn(true);

        controller.addValue(user, 99L, values);
        verify(valueDao).insert("stringRepr", 99L);
    }




    private CSVRecord mockedRecord(int i) {
        CSVRecord record = mock(CSVRecord.class);
        when(record.get("csvHeader1")).thenReturn("val1" + i);
        when(record.get("csvHeader2")).thenReturn("val2" + i);
        when(record.get("csvHeader3")).thenReturn("val3" + i);
        return record;
    }

    private Map<String, String> internalMap(int i) {
        return ImmutableMap.of(
                "internalHeader1", "val1" + i,
                "internalHeader2", "val2" + i,
                "internalHeader3", "val3" + i
        );
    }

    @Test(expected = IllegalAccessException.class)
    public void uploadCSVBadUser() throws Exception {
        SimpleUser user = mock(SimpleUser.class);
        when(user.id()).thenReturn(99L);

        Source source = mock(Source.class);
        when(source.userId()).thenReturn(100L);
        when(sourceDao.findById(123L)).thenReturn(source);

        controller.uploadCSV(user, 123L, null);
    }

    @Test(expected = InconsistentDataException.class)
    public void uploadCSVBadData() throws Exception {
        SimpleUser user = mock(SimpleUser.class);
        when(user.id()).thenReturn(99L);

        Source source = mock(Source.class);
        when(source.userId()).thenReturn(99L);
        when(sourceDao.findById(123L)).thenReturn(source);

        List<CsvRow> rows = mock(List.class);
        InputStream inputStream = mock(InputStream.class);

        CsvInfo info = mock(CsvInfo.class);
        when(wrapper.parse(inputStream, info, source)).thenReturn(rows);

        when(updater.isConsistent(info, rows)).thenReturn(false);

        when(csvInfoDao.findBySourceId(123L)).thenReturn(info);

        controller.uploadCSV(user, 123L, inputStream);
    }

    @Test(expected = IllegalAccessException.class)
    public void setMappingBadUser() throws Exception {
        SimpleUser user = mock(SimpleUser.class);
        when(user.id()).thenReturn(99L);

        Source source = mock(Source.class);
        when(source.userId()).thenReturn(100L);
        when(sourceDao.findById(123L)).thenReturn(source);

        controller.setMapping(user, 123L, null);
    }
}