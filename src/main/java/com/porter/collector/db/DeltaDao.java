package com.porter.collector.db;

import com.porter.collector.model.Delta;
import com.porter.collector.model.DeltaMapper;
import com.porter.collector.model.ImmutableDelta;
import com.porter.collector.model.ValueTypes;
import com.porter.collector.model.Values.ValueType;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.List;

public interface DeltaDao {

    @SqlUpdate("INSERT INTO values (value) VALUES (:value);")
    @GetGeneratedKeys
    long _executeValueInsert(@Bind("value") String value);

    @SqlUpdate("INSERT INTO deltas (name, collection_id, source_id, value, category_id, value_id) VALUES " +
            "(:name, :collectionId, :sourceId, :value, :categoryId, :valueId);")
    @GetGeneratedKeys
    long executeInsert(@Bind("name") String name,
                       @Bind("collectionId") Long collectionId,
                       @Bind("sourceId") Long sourceId,
                       @Bind("value") String value,
                       @Bind("categoryId") Long categoryId,
                       @Bind("valueId") Long valueId);

    default Delta insert(String name, String value, Long collectionId, Long sourceId) {
        return insert(name, value, collectionId, sourceId, null);
    }

    @Transaction
    default Delta insert(String name, String value, Long collectionId, Long sourceId, Long categoryId) {
        long valueId = _executeValueInsert(value);
        Long id = executeInsert(name, collectionId, sourceId, value, categoryId, valueId);
        return ImmutableDelta.builder()
                .id(id)
                .name(name)
                .collectionId(collectionId)
                .sourceId(sourceId)
                .value(value)
                .categoryId(categoryId)
                .valueId(valueId)
                .build();
    }


    @SqlQuery("SELECT * FROM deltas WHERE id=:id")
    @UseRowMapper(DeltaMapper.class)
    Delta findById(@Bind("id") Long id);

    @SqlQuery("SELECT * FROM deltas WHERE source_id=:id")
    @UseRowMapper(DeltaMapper.class)
    List<Delta> findBySourceId(@Bind("id") Long sourceId);
}
