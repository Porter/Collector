package com.porter.collector.db;

import com.porter.collector.model.*;
import com.porter.collector.util.ValueUtil;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.*;

import java.util.ArrayList;
import java.util.List;
import com.porter.collector.util.ValueUtil;

public interface ValueDao {

    @SqlUpdate("INSERT INTO values (value, source_id) VALUES (:value, :sourceId)")
    @GetGeneratedKeys
    long executeInsert(@Bind("value") String value, @Bind("sourceId") long sourceId);

    @SqlBatch("INSERT INTO values (value, source_id) VALUES (:value, :sourceId)")
    @GetGeneratedKeys
    List<Long> executeInsert(@Bind("value") List<String> value, @Bind("sourceId") List<Long> sourceId);

    default List<Value> insert(List<String> values, List<Long> sourceIds) {
        if (values.size() != sourceIds.size()) {
            throw new IllegalArgumentException("Lists must be of same size");
        }
        List<Long> ids = executeInsert(values, sourceIds);

        List<Value> created = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            created.add(ImmutableValue
                    .builder()
                    .id(ids.get(i))
                    .value(values.get(i))
                    .sourceId(sourceIds.get(i))
                    .build()
            );
        }

        return created;
    }

    default Value insert(String value, long sourceId) {
        long id = executeInsert(value, sourceId);

        return ImmutableValue
                .builder()
                .id(id)
                .value(value)
                .sourceId(sourceId)
                .build();
    }

    @SqlQuery("SELECT * FROM values WHERE source_id=:sourceId")
    @UseRowMapper(ValuesMapper.class)
    List<Value> findBySourceId(@Bind("sourceId") Long id);

    @SqlQuery("SELECT * FROM values WHERE id=:id")
    @UseRowMapper(ValuesMapper.class)
    Value findById(@Bind("id") Long id);

    @SqlQuery("SELECT * FROM " +
            "(SELECT values.*, ROW_NUMBER() OVER (ORDER BY id) AS rn FROM values WHERE source_id=:sourceId) AS sub" +
            " WHERE sub.rn > :start AND sub.rn <= :end + 1")
    @UseRowMapper(ValuesMapper.class)
    List<Value> _getRange(@Bind("sourceId") long sourceId, @Bind("start") long start, @Bind("end") long end);

    @SqlQuery("SELECT COUNT(id) FROM values WHERE source_id=:sourceId")
    long _getCount(@Bind("sourceId") long sourceId);

    default List<Value> getRange(long sourceId, long start, long end) {
        if (start < 0 || end < 0) {
            long count = _getCount(sourceId);
            if (start < 0) { start += count; }
            if (end < 0) { end += count - 1; }
        }
        return _getRange(sourceId, start, end);
    }

    default List<Value> insert(List<CsvRow> rows) {
        List<String> values = new ArrayList<>(rows.size());
        List<Long> sourceIds = new ArrayList<>(rows.size());

        for (CsvRow row : rows) {
            values.add(ValueUtil.stringifyValues(row.row()));
            sourceIds.add(row.sourceId());
        }

        return insert(values, sourceIds);
    }
}
