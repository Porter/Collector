package com.porter.collector.db;

import com.porter.collector.model.Delta;
import com.porter.collector.model.DeltaMapper;
import com.porter.collector.model.ImmutableDelta;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public abstract class DeltaDao {

    @SqlUpdate("INSERT INTO deltas (name, collection_id, source_id, amount, category_id) VALUES " +
            "(:name, :collectionId, :sourceId, :amount, :categoryId);")
    @GetGeneratedKeys
    abstract long executeInsert(@Bind("name") String name,
                                @Bind("amount") Long amount,
                                @Bind("collectionId") Long collectionId,
                                @Bind("sourceId") Long sourceId,
                                @Bind("categoryId") Long categoryId);

    public Delta insert(String name, Long amount, Long collectionId, Long sourceId) {
        return insert(name, amount, collectionId, sourceId, null);
    }

    public Delta insert(String name, Long amount, Long collectionId, Long sourceId, Long categoryId) {
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
    @Mapper(DeltaMapper.class)
    public abstract Delta findById(@Bind("id") Long id);
}
