package com.porter.collector.controller;

import com.porter.collector.csv.CsvInfo;
import com.porter.collector.csv.CsvParserWrapper;
import com.porter.collector.csv.CsvUpdater;
import com.porter.collector.db.*;
import com.porter.collector.exception.InconsistentDataException;
import com.porter.collector.exception.UnableToProcessCsvException;
import com.porter.collector.model.*;
import com.porter.collector.util.ValueValidator;
import com.porter.collector.values.ValueType;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SourcesController {

    private final SourceDao sourceDao;
    private final ValueDao valueDao;
    private final CsvRowDao csvRowDao;
    private final CsvInfoDao csvInfoDao;
    private final CustomTypeDao customTypeDao;
    private final CsvUpdater updater;
    private final CsvParserWrapper parserWrapper;
    private final ValueValidator validator;

    public SourcesController(SourceDao sourceDao, ValueDao valueDao, CsvRowDao csvRowDao,
                             CsvInfoDao csvInfoDao, CustomTypeDao customTypeDao, CsvParserWrapper parserWrapper,
                             ValueValidator validator, CsvUpdater updater) {
        this.sourceDao = sourceDao;
        this.valueDao = valueDao;
        this.csvRowDao = csvRowDao;
        this.csvInfoDao = csvInfoDao;
        this.customTypeDao = customTypeDao;
        this.updater = updater;
        this.parserWrapper = parserWrapper;
        this.validator = validator;
    }

    public List<Source> getAllFromUser(SimpleUser user) {
        return sourceDao.findAllFromUser(user.id());
    }

    public Source getById(SimpleUser requesting, @PathParam("id") Long id) throws IllegalAccessException {
        Source source = sourceDao.findById(id);

        checkSourceExists(source);
        checkUserOwnsSource(requesting, source);

        return source;
    }

    public List<Value> getAllValues(SimpleUser requesting, long sourceId) throws IllegalAccessException {
        Source source = sourceDao.findById(sourceId);

        checkSourceExists(source);
        checkUserOwnsSource(requesting, source);

        return valueDao.findBySourceId(sourceId);
    }

    private void checkSourceExists(Source source) {
        if (source == null) {
            throw new NotFoundException("That source no longer exists");
        }
    }

    public Value addValue(SimpleUser user, long sourceId, MultivaluedMap<String, String> value) throws IllegalAccessException {
        Source source = sourceDao.findById(sourceId);

        checkSourceExists(source);
        checkUserOwnsSource(user, source);

        if (!validator.isValid(source, validator.fromMultivaluedMap(value))) {
            throw new IllegalArgumentException("Invalid data");
        }

        String val = validator.stringRepr(source, validator.fromMultivaluedMap(value));
        return valueDao.insert(val, sourceId);
    }

    private void checkUserOwnsSource(SimpleUser user, Source source) throws IllegalAccessException {
        if (user.id() != source.userId()) {
            throw new IllegalAccessException("You no longer have access to this source");
        }
    }

    public List<Value> uploadCSV(SimpleUser uploader, long sourceId, InputStream inputStream)
            throws IOException, IllegalAccessException, UnableToProcessCsvException, InconsistentDataException {

        Source source = sourceDao.findById(sourceId);
        checkUserOwnsSource(uploader, source);

        CsvInfo info = csvInfoDao.findBySourceId(source.id());

        List<CsvRow> parsedRows = parserWrapper.parse(inputStream, info, source);

        if (!updater.isConsistent(info, parsedRows)) {
            throw new InconsistentDataException();
        }

        List<CsvRow> newRows = updater.newRows(info, parsedRows);
        Map<String, ValueType> newInfo = updater.getInfo(parsedRows);

        csvRowDao.insert(newRows);
        csvInfoDao.updateCsvInfo(info.sourceId(), newInfo, parsedRows.size());

        return valueDao.insert(newRows);
    }

    public List<String> setMapping(SimpleUser user, long sourceId, MultivaluedMap<String, String> map)
        throws IllegalAccessException {

        Source source = sourceDao.findById(sourceId);
        checkSourceExists(source);
        checkUserOwnsSource(user, source);

        UsersCustomType customType = customTypeDao.findBySourceId(sourceId);

        Set<String> requiredKeys = customType.type().getTypes().keySet();
        Set<String> actualKeys = map.keySet();

        if (!requiredKeys.equals(actualKeys)) {
            throw new IllegalStateException("keys required: " + requiredKeys);
        }

        csvInfoDao.insert(sourceId, validator.fromMultivaluedMap(map), validator.emptyInfo(customType.type()), 0);

        return new ArrayList<>(map.keySet());
    }
}
