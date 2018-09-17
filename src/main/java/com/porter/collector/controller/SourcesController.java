package com.porter.collector.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.porter.collector.db.*;
import com.porter.collector.exception.CsvMappingNotSetException;
import com.porter.collector.model.*;
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
import java.util.stream.Collectors;

public class SourcesController {

    private final SourceDao sourceDao;
    private final ValueDao valueDao;
    private final CsvRowDao csvRowDao;
    private final CsvColumnMappingDao csvColumnMappingDao;
    private final CustomTypeDao customTypeDao;

    public SourcesController(SourceDao sourceDao, ValueDao valueDao, CsvRowDao csvRowDao,
                             CsvColumnMappingDao csvColumnMappingDao, CustomTypeDao customTypeDao) {
        this.sourceDao = sourceDao;
        this.valueDao = valueDao;
        this.csvRowDao = csvRowDao;
        this.csvColumnMappingDao = csvColumnMappingDao;
        this.customTypeDao = customTypeDao;
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

                try {
                    value.putSingle(key, t.parse(keysValue).stringify());
                } catch (Exception e) {
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

    private String stringRepr(Source source, MultivaluedMap<String, String> value) {
        if (source.type() == null) {
            ObjectMapper mapper = Jackson.newObjectMapper();
            try {
                Map<String, String> singleMap = new HashMap<>();
                value.forEach((key, val) -> {
                    singleMap.put(key, val.get(0));
                });

                return mapper.writeValueAsString(singleMap);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return "{}";
            }
        } else {
            return value.getFirst("amount");
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

        String val = stringRepr(source, value);
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

        ObjectMapper mapper = Jackson.newObjectMapper();
        List<Value> valueList = new ArrayList<>();
        for (CSVRecord record : parser) {
            Map<String, String> internalMap = new HashMap<>();
            mapping.mapping().forEach((internalKey, csvKey) ->  internalMap.put(internalKey, record.get(csvKey)));

            String value = mapper.writeValueAsString(internalMap);
            valueList.add(valueDao.insert(value, sourceId));
        }

        return valueList;
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
}
