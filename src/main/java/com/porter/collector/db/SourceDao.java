package com.porter.collector.db;

import com.porter.collector.model.*;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.List;

public abstract class SourceDao {

    @SqlUpdate("INSERT INTO sources (name, collection_id, user_id, type) VALUES (:name, :collection_id, :user_id, :type);")
    @GetGeneratedKeys
    abstract long executeInsert(@Bind("name") String name,
                                @Bind("user_id") Long userId,
                                @Bind("collection_id") Long collectionId,
                                @Bind("type") Long type);

    public Source insert(String name, Long userId, Long collectionId, ValueTypes type) throws IllegalAccessException {
        if (confirmUserOwnsCollection(collectionId, userId) == null) {
            throw new IllegalAccessException("You can no longer modify that collection");
        }
        // TODO make type an int in db
        Long id = executeInsert(name, userId, collectionId, (long) type.ordinal());
        return ImmutableSource.builder()
                .id(id)
                .name(name)
                .collectionId(collectionId)
                .userId(userId)
                .type(type)
                .build();
    }

    @SqlQuery("SELECT id FROM collections WHERE id=:id AND user_id=:userId")
    abstract Long confirmUserOwnsCollection(@Bind("id") long collectionId, @Bind("userId") long userId);


    @SqlQuery("SELECT * FROM sources WHERE id=:id")
    @Mapper(SourcesMapper.class)
    public abstract Source findById(@Bind("id") Long id);

    @SqlQuery("SELECT * FROM sources")
    @Mapper(SourcesMapper.class)
    public abstract List<Source> findAll();

    @SqlQuery("SELECT * FROM sources where user_id=:user_id")
    @Mapper(SourcesMapper.class)
    public abstract List<Source> findAllFromUser(@Bind("user_id") Long user_id);
}
