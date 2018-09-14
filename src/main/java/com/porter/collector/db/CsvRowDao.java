package com.porter.collector.db;

import com.porter.collector.model.*;
import com.porter.collector.parser.Parser;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.util.List;
import java.util.stream.Collectors;

public interface CsvRowDao {

    @SqlUpdate("INSERT INTO csv (row, rowNumber, processed, source_id) VALUES (:row, :rowNumber, :processed, :sourceId)")
    @GetGeneratedKeys
    long executeInsert(
            @Bind("row") String row,
            @Bind("rowNumber") int rowNum,
            @Bind("processed") boolean processed,
            @Bind("sourceId") long sourceId);

    @SqlQuery("SELECT * FROM csv WHERE source_id=:sourceId")
    @UseRowMapper(CsvRowMapper.class)
    List<CsvRow> findAllFromSource(@Bind("sourceId") Long id);

    @SqlQuery("SELECT * FROM csv WHERE id=:id")
    @UseRowMapper(CsvRowMapper.class)
    CsvRow findById(@Bind("id") Long id);

    default CsvRow insert(String row, int rowNum, boolean processed, long sourceId) {
        long id = executeInsert(row, rowNum, processed, sourceId);

        return ImmutableCsvRow
                .builder()
                .id(id)
                .rowNumber(rowNum)
                .row(row)
                .processed(processed)
                .sourceId(sourceId)
                .build();
    }
}
