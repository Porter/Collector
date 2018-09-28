package com.porter.collector.db;

import com.porter.collector.model.CsvRow;
import com.porter.collector.model.ImmutableValue;
import com.porter.collector.model.Value;
import com.porter.collector.model.ValuesMapper;
import com.porter.collector.util.ValueValidator;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.*;

import java.util.ArrayList;
import java.util.List;

public class ValueDao {

    private final Jdbi jdbi;
    private final ValueValidator validator;
    private final ValuesMapper valuesMapper;

    public ValueDao(Jdbi jdbi, ValueValidator validator, ValuesMapper valuesMapper) {
        this.jdbi = jdbi;
        this.validator = validator;
        this.valuesMapper = valuesMapper;
    }

    public List<Value> insert(List<String> values, List<Long> sourceIds) {
        if (values.size() != sourceIds.size()) {
            throw new IllegalArgumentException("Lists must be of same size");
        }

        List<Long> ids = jdbi.withHandle(handle -> {
                    PreparedBatch batch = handle
                            .prepareBatch("INSERT INTO values (value, source_id) VALUES (:value, :sourceId)");

                    for (int i = 0; i < values.size(); i++) {
                        batch
                                .bind("value", values.get(i))
                                .bind("sourceId", sourceIds.get(i))
                                .add();
                    }

                    return batch.executeAndReturnGeneratedKeys("id")
                            .mapTo(Long.class)
                            .list();
                }
        );

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

    public Value insert(String value, long sourceId) {
        Long id = jdbi.withHandle(handle ->
                handle
                        .createUpdate("INSERT INTO values (value, source_id) VALUES (:value, :sourceId)")
                        .bind("value", value)
                        .bind("sourceId", sourceId)
                        .executeAndReturnGeneratedKeys("id")
                        .mapTo(Long.class)
                        .findOnly()
        );

        return ImmutableValue
                .builder()
                .id(id)
                .value(value)
                .sourceId(sourceId)
                .build();
    }

    public List<Value> findBySourceId(@Bind("sourceId") Long id) {
        return jdbi.withHandle(handle -> handle
                .createQuery("SELECT * FROM values WHERE source_id=:sourceId")
                .bind("sourceId", id)
                .map(valuesMapper)
                .list()
        );
    }

    public Value findById(@Bind("id") Long id) {
        return jdbi.withHandle(handle -> handle
                .createQuery("SELECT * FROM values WHERE id=:id")
                .bind("id", id)
                .map(valuesMapper)
                .findOnly()
        );
    }

    public List<Value> _getRange(long sourceId, long start, long end) {
        return jdbi.withHandle(handle -> handle
                .createQuery("SELECT * FROM " +
                        "(SELECT values.*, ROW_NUMBER() OVER (ORDER BY id) AS rn FROM values WHERE source_id=:sourceId) AS sub" +
                        " WHERE sub.rn > :start AND sub.rn <= :end + 1")
                .bind("sourceId", sourceId)
                .bind("start", start)
                .bind("end", end)
                .map(valuesMapper)
                .list()
        );
    }

    @SqlQuery("SELECT COUNT(id) FROM values WHERE source_id=:sourceId")
    public long _getCount(@Bind("sourceId") long sourceId) {
        return jdbi.withHandle(handle -> handle
                .createQuery("SELECT COUNT(id) FROM values WHERE source_id=:sourceId")
                .bind("sourceId", sourceId)
                .mapTo(Long.class)
                .findOnly()
        );
    }

    public List<Value> getRange(long sourceId, long start, long end) {
        if (start < 0 || end < 0) {
            long count = _getCount(sourceId);
            if (start < 0) { start += count; }
            if (end < 0) { end += count - 1; }
        }
        return _getRange(sourceId, start, end);
    }

    public List<Value> insert(List<CsvRow> rows) {
        List<String> values = new ArrayList<>(rows.size());
        List<Long> sourceIds = new ArrayList<>(rows.size());

        for (CsvRow row : rows) {
            values.add(validator.stringRepr(row.row()));
            sourceIds.add(row.sourceId());
        }

        return insert(values, sourceIds);
    }
}
