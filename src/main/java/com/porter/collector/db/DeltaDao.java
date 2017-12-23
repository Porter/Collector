package com.porter.collector.db;

import com.porter.collector.errors.*;
import com.porter.collector.model.*;
import com.porter.collector.util.Email;
import org.mindrot.jbcrypt.BCrypt;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public abstract class DeltaDao {

    @SqlUpdate("INSERT INTO deltas (name, collection_id, source_id) VALUES (:name, :collectionId, :sourceId);")
    @GetGeneratedKeys
    abstract long executeInsert(@Bind("name") String name,
                                @Bind("collectionId") Long collectionId,
                                @Bind("sourceId") Long sourceId);

    public Delta insert(String name, Long collectionId, Long sourceId) {
        Long id = executeInsert(name, collectionId, sourceId);
        return ImmutableDelta.builder()
                .id(id)
                .name(name)
                .collectionId(collectionId)
                .sourceId(sourceId)
                .build();
    }


    @SqlQuery("SELECT * FROM deltas WHERE id=:id")
    @Mapper(DeltaMapper.class)
    public abstract Delta findById(@Bind("id") Long id);
}
