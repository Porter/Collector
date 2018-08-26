package com.porter.collector.db;

import com.porter.collector.model.*;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.util.List;

public interface SourceDao {

    @SqlUpdate("INSERT INTO sources (name, collection_id, user_id, type) VALUES (:name, :collection_id, :user_id, :type);")
    @GetGeneratedKeys
    long executeInsert(@Bind("name") String name,
                                @Bind("user_id") Long userId,
                                @Bind("collection_id") Long collectionId,
                                @Bind("type") int type);


    default Source insert(String name, Long userId, Long collectionId, ValueTypes type) throws IllegalAccessException {
        if (confirmUserOwnsCollection(collectionId, userId) == null) {
            throw new IllegalAccessException("You can no longer modify that collection");
        }
        Long id = executeInsert(name, userId, collectionId, type.ordinal());
        return ImmutableSource.builder()
                .id(id)
                .name(name)
                .collectionId(collectionId)
                .userId(userId)
                .type(type)
                .build();
    }

    @SqlQuery("SELECT id FROM collections WHERE id=:id AND user_id=:userId")
    Long confirmUserOwnsCollection(@Bind("id") long collectionId, @Bind("userId") long userId);


    @SqlQuery("SELECT * FROM sources WHERE id=:id")
    @UseRowMapper(SourcesMapper.class)
    Source findById(@Bind("id") Long id);

    @SqlQuery("SELECT * FROM sources")
    @UseRowMapper(SourcesMapper.class)
    List<Source> findAll();

    @SqlQuery("SELECT * FROM sources where user_id=:user_id")
    @UseRowMapper(SourcesMapper.class)
    List<Source> findAllFromUser(@Bind("user_id") Long user_id);
}
