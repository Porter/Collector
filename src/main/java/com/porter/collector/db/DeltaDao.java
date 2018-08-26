package com.porter.collector.db;

import com.porter.collector.model.Delta;
import com.porter.collector.model.DeltaMapper;
import com.porter.collector.model.ImmutableDelta;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.util.List;

public interface DeltaDao {

    @SqlUpdate("INSERT INTO deltas (name, collection_id, source_id, amount, category_id) VALUES " +
            "(:name, :collectionId, :sourceId, :amount, :categoryId);")
    @GetGeneratedKeys
    long executeInsert(@Bind("name") String name,
                                @Bind("amount") Long amount,
                                @Bind("collectionId") Long collectionId,
                                @Bind("sourceId") Long sourceId,
                                @Bind("categoryId") Long categoryId);

    default Delta insert(String name, Long amount, Long collectionId, Long sourceId) {
        return insert(name, amount, collectionId, sourceId, null);
    }

    default Delta insert(String name, Long amount, Long collectionId, Long sourceId, Long categoryId) {
        Long id = executeInsert(name, amount, collectionId, sourceId, categoryId);
        return ImmutableDelta.builder()
                .id(id)
                .name(name)
                .collectionId(collectionId)
                .sourceId(sourceId)
                .amount(amount)
                .categoryId(categoryId)
                .build();
    }


    @SqlQuery("SELECT * FROM deltas WHERE id=:id")
    @UseRowMapper(DeltaMapper.class)
    Delta findById(@Bind("id") Long id);

    @SqlQuery("SELECT * FROM deltas WHERE source_id=:id")
    @UseRowMapper(DeltaMapper.class)
    List<Delta> findBySourceId(@Bind("id") Long sourceId);
}
