package com.porter.collector.db;

import com.porter.collector.model.*;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.util.List;

public interface ValueDao {

    @SqlUpdate("INSERT INTO values (value, source_id) VALUES (:value, :sourceId)")
    @GetGeneratedKeys
    long executeInsert(@Bind("value") String value, @Bind("sourceId") long sourceId);

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

    @SqlQuery("SELECT COUNT(id) FROM values WHERE source_id=:souceId")
    long _getCount(@Bind("souceId") long sourceId);

    default List<Value> getRange(long sourceId, long start, long end) {
        if (start < 0 || end < 0) {
            long count = _getCount(sourceId);
            if (start < 0) { start += count; }
            if (end < 0) { end += count - 1; }
        }
        return _getRange(sourceId, start, end);
    }
}
