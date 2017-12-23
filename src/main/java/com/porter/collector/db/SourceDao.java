package com.porter.collector.db;

import com.porter.collector.model.*;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public abstract class SourceDao {

    @SqlUpdate("INSERT INTO sources (name, collection_id) VALUES (:name, :collectionId);")
    @GetGeneratedKeys
    abstract long executeInsert(@Bind("name") String name,
                                @Bind("collectionId") Long collectionId);

    public Source insert(String name, Long collectionId) {
        Long id = executeInsert(name, collectionId);
        return ImmutableSource.builder()
                .id(id)
                .name(name)
                .collectionId(collectionId)
                .build();
    }


    @SqlQuery("SELECT * FROM sources WHERE id=:id")
    @Mapper(SourcesMapper.class)
    public abstract Source findById(@Bind("id") Long id);
}
