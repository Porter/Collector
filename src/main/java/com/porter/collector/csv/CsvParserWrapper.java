package com.porter.collector.csv;

import com.porter.collector.exception.CsvMappingNotSetException;
import com.porter.collector.exception.MissingHeadersException;
import com.porter.collector.exception.UnableToProcessCsvException;
import com.porter.collector.model.CsvRow;
import com.porter.collector.model.ImmutableCsvRow;
import com.porter.collector.model.Source;
import com.porter.collector.util.Provider;
import com.porter.collector.util.ValueValidator;
import com.porter.collector.values.ValueType;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class CsvParserWrapper {

    private final ValueValidator validator;
    private final Provider<CSVParser, InputStream> parserProvider;

    public CsvParserWrapper(ValueValidator validator, Provider<CSVParser, InputStream> parserProvider) {
        this.validator = validator;
        this.parserProvider = parserProvider;
    }

    CSVParser getCSVParser(InputStream inputStream, CsvInfo info) throws IOException, UnableToProcessCsvException {
        CSVParser parser = parserProvider.get(inputStream);

        if (info == null) {
            throw new CsvMappingNotSetException(new ArrayList<>(parser.getHeaderMap().keySet()));
        }

        Set<String> csvKeySet = parser.getHeaderMap().keySet();
        Set<String> internalKeySet = info.columnMapping().keySet();
        Set<String> mappedCsvKeySet = internalKeySet.stream()
                .map(info.columnMapping()::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (!csvKeySet.containsAll(mappedCsvKeySet)) {
            mappedCsvKeySet.removeAll(csvKeySet);
            throw new MissingHeadersException(new ArrayList<>(mappedCsvKeySet));
        }

        return parser;
    }

    List<CsvRow> getRows(CsvInfo info, CSVParser parser, Source source) throws IOException {
        List<CsvRow> parsedRows = new ArrayList<>();

        List<CSVRecord> records = parser.getRecords();
        Collections.reverse(records);
        int recordNumber = 1;
        for (CSVRecord record : records) {
            Map<String, String> internalMap = new HashMap<>();
            info.columnMapping().forEach((internalKey, csvKey) ->  internalMap.put(internalKey, record.get(csvKey)));

            Map<String, ValueType> mappedValues = validator.parseValues(internalMap, source.customType());

            parsedRows.add(ImmutableCsvRow.builder()
                    .sourceId(source.id())
                    .rowNumber(recordNumber++)
                    .row(mappedValues)
                    .processed(true)
                    .build()
            );
        }

        return parsedRows;
    }


    public List<CsvRow> parse(InputStream inputStream, CsvInfo info, Source source)
            throws IOException, UnableToProcessCsvException {

        CSVParser parser = getCSVParser(inputStream, info);
        return getRows(info, parser, source);
    }
}
