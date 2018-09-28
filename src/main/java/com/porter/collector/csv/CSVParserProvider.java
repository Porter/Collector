package com.porter.collector.csv;

import com.porter.collector.util.Provider;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CSVParserProvider implements Provider<CSVParser, InputStream> {

    private final CSVFormat csvFormat;

    public CSVParserProvider(CSVFormat csvFormat) {
        this.csvFormat = csvFormat;
    }
    @Override
    public CSVParser get(InputStream arg) {
        try {
            return new CSVParser(new InputStreamReader(arg), csvFormat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
