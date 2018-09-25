package com.porter.collector.csv;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.porter.collector.exception.MissingHeadersException;
import com.porter.collector.model.Source;
import com.porter.collector.util.ValueValidator;
import com.porter.collector.values.CustomType;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CsvParserWrapperTest {

    private ValueValidator validator;
    private CSVParserProvider parserProvider;
    private CsvParserWrapper wrapper;

    @Before
    public void setUp() {
        validator = mock(ValueValidator.class);
        parserProvider = mock(CSVParserProvider.class);
        wrapper = new CsvParserWrapper(validator, parserProvider);
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

    @Test
    public void getCSVParser() throws Exception {
        InputStream stream = mock(InputStream.class);
        CsvInfo info = mock(CsvInfo.class);
        CSVParser parser = mock(CSVParser.class);

        when(parserProvider.get(stream)).thenReturn(parser);
        when(parser.getHeaderMap()).thenReturn(ImmutableMap.of(
                "csvHeader1", 1,
                "csvHeader2", 2
        ));
        when(info.columnMapping()).thenReturn(ImmutableMap.of(
                "internalHeader1", "csvHeader1",
                "internalHeader2", "csvHeader2"
        ));

         assertEquals(parser, wrapper.getCSVParser(stream, info));
    }

    @Test
    public void getCSVParserExtraCsvHeaders() throws Exception {
        InputStream stream = mock(InputStream.class);
        CsvInfo info = mock(CsvInfo.class);
        CSVParser parser = mock(CSVParser.class);
        when(parserProvider.get(stream)).thenReturn(parser);
        when(parser.getHeaderMap()).thenReturn(ImmutableMap.of(
                "csvHeader1", 1,
                "csvHeader2", 2,
                "csvHeader3", 2
        ));
        when(info.columnMapping()).thenReturn(ImmutableMap.of(
                "internalHeader1", "csvHeader1",
                "internalHeader2", "csvHeader2"
        ));

        assertEquals(parser, wrapper.getCSVParser(stream, info));
    }

    @Test
    public void getCSVParserMissingCsvHeader() throws Exception {
        InputStream stream = mock(InputStream.class);
        CsvInfo info = mock(CsvInfo.class);
        CSVParser parser = mock(CSVParser.class);
        when(parserProvider.get(stream)).thenReturn(parser);
        when(parser.getHeaderMap()).thenReturn(ImmutableMap.of(
                "csvHeader1", 1,
                "csvHeader3", 2
        ));
        when(info.columnMapping()).thenReturn(ImmutableMap.of(
                "internalHeader1", "csvHeader1",
                "internalHeader2", "csvHeader2"
        ));

        try {
            assertEquals(parser, wrapper.getCSVParser(stream, info));
            fail("Should have thrown");
        } catch (MissingHeadersException e) {
            assertEquals(e.getMissingHeaders(), ImmutableList.of("csvHeader2"));
        }
    }

    @Test
    public void allRows() throws Exception {
        CsvInfo info = mock(CsvInfo.class);
        CSVParser parser = mock(CSVParser.class);
        Source source = mock(Source.class);

        CSVRecord record1 = mockedRecord(1);
        CSVRecord record2 = mockedRecord(2);
        CSVRecord record3 = mockedRecord(3);
        List<CSVRecord> records = new ArrayList<>();
        records.add(record1);
        records.add(record2);
        records.add(record3);
        when(parser.getRecords()).thenReturn(records);

        when(info.columnMapping()).thenReturn(ImmutableMap.of(
                "internalHeader1", "csvHeader1",
                "internalHeader2", "csvHeader2",
                "internalHeader3", "csvHeader3"
        ));

        CustomType customType = mock(CustomType.class);
        when(source.customType()).thenReturn(customType);

        wrapper.getRows(info, parser, source);
        verify(validator).parseValues(internalMap(1), customType);
        verify(validator).parseValues(internalMap(2), customType);
        verify(validator).parseValues(internalMap(3), customType);
    }
}