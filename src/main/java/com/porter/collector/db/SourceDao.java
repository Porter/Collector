package com.porter.collector.db;

import com.porter.collector.model.*;

import com.porter.collector.values.CustomType;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.util.List;

public interface SourceDao {

    @SqlUpdate("INSERT INTO sources (name, collection_id, user_id, type, custom_type_id, external) " +
            "VALUES (:name, :collection_id, :user_id, :type, :customType, :external);")
    @GetGeneratedKeys
    long executeInsert(@Bind("name") String name,
                                @Bind("user_id") Long userId,
                                @Bind("collection_id") Long collectionId,
                                @Bind("type") int type,
                                @Bind("customType") Long customType,
                                @Bind("external") boolean external);

    default int _getOrdinal(Enum e) {
        if (e != null) { return e.ordinal(); }
        return -1;
    }

    default Long _getId(UsersCustomType type) {
        if (type != null) { return type.id(); }
        return null;
    }

    default Source insert(String name, Long userId, Long collectionId, ValueTypes type, UsersCustomType usersCustomType,
                          boolean external) {
        Long id;
        try {
            id = executeInsert(name, userId, collectionId, _getOrdinal(type), _getId(usersCustomType), external);
        } catch (UnableToExecuteStatementException e) {
            if (e.getMessage().contains("violates foreign key constraint \"sources_users_many_to_one\"")) {
                throw new IllegalStateException(e);
            }
            throw e;
        }

        CustomType customType = null;
        if (usersCustomType != null) { customType = usersCustomType.type(); }
        return ImmutableSource.builder()
                .id(id)
                .name(name)
                .collectionId(collectionId)
                .userId(userId)
                .type(type)
                .customType(customType)
                .external(external)
                .build();
    }

    @SqlQuery("SELECT id FROM collections WHERE id=:id AND user_id=:userId")
    Long confirmUserOwnsCollection(@Bind("id") long collectionId, @Bind("userId") long userId);


    @SqlQuery("SELECT sources.*, custom_types.type AS custom_type FROM sources LEFT JOIN custom_types ON " +
            "sources.custom_type_id = custom_types.id WHERE sources.id=:id")
    @UseRowMapper(SourcesMapper.class)
    Source findById(@Bind("id") Long id);

    @SqlQuery("SELECT sources.*, custom_types.type AS custom_type FROM sources LEFT JOIN custom_types ON " +
            "sources.custom_type_id = custom_types.id")
    @UseRowMapper(SourcesMapper.class)
    List<Source> findAll();

    @SqlQuery("SELECT sources.*, custom_types.type AS custom_type FROM sources LEFT JOIN custom_types ON " +
            "sources.custom_type_id = custom_types.id WHERE sources.user_id=:user_id")
    @UseRowMapper(SourcesMapper.class)
    List<Source> findAllFromUser(@Bind("user_id") Long user_id);

    @SqlQuery("SELECT sources.*, custom_types.type AS custom_type FROM sources LEFT JOIN custom_types ON " +
            "sources.custom_type_id = custom_types.id WHERE sources.user_id=:userId AND sources.name=:source")
    @UseRowMapper(SourcesMapper.class)
    Source findByUsersSource(@Bind("userId") long userId, @Bind("source") String source);
}
