package com.porter.collector.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.porter.collector.csv.CsvRowsInfo;
import com.porter.collector.csv.CsvUpdater;
import com.porter.collector.db.*;
import com.porter.collector.exception.CsvMappingNotSetException;
import com.porter.collector.model.*;
import com.porter.collector.util.IteratorUtil;
import com.porter.collector.values.CustomType;
import com.porter.collector.values.ValueType;
import io.dropwizard.jackson.Jackson;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.Collection;
import java.util.stream.Collectors;

public class SourcesController {

    private final SourceDao sourceDao;
    private final ValueDao valueDao;
    private final CsvRowDao csvRowDao;
    private final CsvColumnMappingDao csvColumnMappingDao;
    private final CustomTypeDao customTypeDao;
    private final CsvUpdater updater;

    public SourcesController(SourceDao sourceDao, ValueDao valueDao, CsvRowDao csvRowDao,
                             CsvColumnMappingDao csvColumnMappingDao, CustomTypeDao customTypeDao) {
        this.sourceDao = sourceDao;
        this.valueDao = valueDao;
        this.csvRowDao = csvRowDao;
        this.csvColumnMappingDao = csvColumnMappingDao;
        this.customTypeDao = customTypeDao;
        this.updater = new CsvUpdater();
    }

    public List<Source> getAllFromUser(SimpleUser user) {
        return sourceDao.findAllFromUser(user.id());
    }

    public Source getById(SimpleUser requesting, @PathParam("id") Long id) throws IllegalAccessException {
        Source source = sourceDao.findById(id);
        if (source.userId() != requesting.id()) {
            throw new IllegalAccessException();
        }
        return source;
    }

    public List<Value> getAllValues(SimpleUser requesting, long sourceId) throws IllegalAccessException {
        Source source = sourceDao.findById(sourceId);
        if (source == null) {
            return null;
        }
        if (source.userId() != requesting.id()) {
            throw new IllegalAccessException();
        }

        return valueDao.findBySourceId(sourceId);
    }

    private boolean isValid(Source source, MultivaluedMap<String, String> value) {
        ValueTypes type = source.type();
        if (type == null) {
            CustomType customType = source.customType();
            if (!customType.getTypes().keySet().equals(value.keySet())) { return false; }
            Set<String> keySet = value.keySet();

            for (String key : keySet) {
                if (value.get(key).size() != 1) { return false; }
                ValueTypes valueTypes = customType.getTypes().get(key);

                ValueType t = ValueTypes.getMap().get(valueTypes);
                String keysValue = value.getFirst(key);

                if (!t.isValid(keysValue)) {
                    return false;
                }
            }
            return true;
        }
        else {
            if (!value.containsKey("amount")) {
                return false;
            }
            List<String> values = value.get("amount");
            if (values.size() != 1) {
                return false;
            }
            return ValueTypes.getMap().get(type).isValid(values.get(0));
        }
    }

    private Map<String, ValueType> parseValues(Source source, Map<String, String> values) {
        CustomType type = source.customType();
        Map<String, ValueType> map = new HashMap<>();
        type.getTypes().forEach((key, valueTypes) -> {
            ValueType valueType;
            try {
                valueType = ValueTypes.getMap().get(valueTypes).parse(values.get(key));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            map.put(key, valueType);
        });

        return map;
    }

    private String stringifyValues(Map<String, ValueType> values) {
        Map<String, String> map = new HashMap<>();
        values.forEach((key, valueType) -> map.put(key, valueType.stringify()));

        ObjectMapper mapper = Jackson.newObjectMapper();
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String stringRepr(Source source, Map<String, String> value) {
        if (source.type() == null) {
            Map<String, ValueType> parsed = parseValues(source, value);
            return stringifyValues(parsed);
        } else {
            ValueTypes types = source.type();
            try {
                return ValueTypes.getMap().get(types).parse(value.get("amount")).stringify();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Value addValue(SimpleUser user, long sourceId, MultivaluedMap<String, String> value) {
        Source source = sourceDao.findById(sourceId);
        if (source == null) {
            return null;
        }
        if (user.id() != source.userId()) {
            return null;
        }
        if (!isValid(source, value)) {
            return null;
        }

        String val = stringRepr(source, fromMultivaluedMap(value));
        return valueDao.insert(val, sourceId);
    }

    public List<Value> getRange(SimpleUser requester, long sourceId, long start, long end) {
        Source source = sourceDao.findById(sourceId);
        if (source == null) {
            return null;
        }
        if (requester.id() != source.userId()) {
            return null;
        }

        return valueDao.getRange(sourceId, start, end);
    }

    public List<Value> uploadCSV(SimpleUser uploader, long sourceId, InputStream inputStream)
            throws IOException, IllegalAccessException, CsvMappingNotSetException {

        Source source = sourceDao.findById(sourceId);
        if (uploader.id() != source.userId()) {
            throw new IllegalAccessException("You no longer have access to this source");
        }

        CsvColumnMapping mapping = csvColumnMappingDao.findBySourceId(sourceId);


        Reader reader = new InputStreamReader(inputStream);
        CSVParser parser = new CSVParser(reader, CSVFormat.RFC4180.withFirstRecordAsHeader());

        if (mapping == null) {
            throw new CsvMappingNotSetException(new ArrayList<>(parser.getHeaderMap().keySet()));
        }

        Set<String> csvKeySet = parser.getHeaderMap().keySet();
        Set<String> internalKeySet = mapping.mapping().keySet();
        Set<String> mappedCsvKeySet = internalKeySet.stream()
                .map(mapping.mapping()::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (!csvKeySet.containsAll(mappedCsvKeySet)) {
            return null;
        }

        List<String> values = new ArrayList<>();
        List<Long> sourceIds = new ArrayList<>();
        List<Map<String, ValueType>> rows = new ArrayList<>();
        List<Integer> rowNumbers = new ArrayList<>();

        for (CSVRecord record : parser) {
            Map<String, String> internalMap = new HashMap<>();
            mapping.mapping().forEach((internalKey, csvKey) ->  internalMap.put(internalKey, record.get(csvKey)));

            Map<String, ValueType> mappedValues = parseValues(source, internalMap);
            String json = stringifyValues(mappedValues);

            rows.add(mappedValues);
            values.add(json);
            sourceIds.add(sourceId);
            //TODO make csv row num a bigint
            rowNumbers.add((int) record.getRecordNumber());

        }

        List<Boolean> allTrue = IteratorUtil.listFromItrerator(IteratorUtil.nOf(rows.size(), true));
        List<CsvRow> inserted = csvRowDao.insert(rows, rowNumbers, allTrue, sourceIds);
        CsvRowsInfo info = updater.getInfo(inserted);
        csvRowDao.insert(info);
        return valueDao.insert(values, sourceIds);
    }

    public List<String> setMapping(SimpleUser user, long sourceId, MultivaluedMap<String, String> map)
        throws IllegalAccessException {

        Source source = sourceDao.findById(sourceId);
        if (user.id() != source.userId()) {
            throw new IllegalAccessException("You no longer have access to this source");
        }

        UsersCustomType customType = customTypeDao.findBySourceId(sourceId);

        Set<String> requiredKeys = customType.type().getTypes().keySet();

        Set<String> actualKeys = map.keySet();

        if (!requiredKeys.equals(actualKeys)) {
            throw new IllegalStateException("keys required: " + requiredKeys);
        }

        csvColumnMappingDao.insert(sourceId, fromMultivaluedMap(map));

        return new ArrayList<>(map.keySet());
    }

    private Map<String, String> fromMultivaluedMap(MultivaluedMap<String, String> map) throws IllegalArgumentException {
        Map<String, String> singleValuedMap = new HashMap<>();
        map.forEach((key, value) -> {
            if (value.size() != 1) {
                throw new IllegalArgumentException("Multivaluedmap should be convertable to normal");
            }

            singleValuedMap.put(key, value.get(0));
        });

        return singleValuedMap;
    }
}
