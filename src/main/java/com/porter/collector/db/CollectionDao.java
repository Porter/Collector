package com.porter.collector.db;

import com.porter.collector.exception.CollectionExistsException;
import com.porter.collector.model.*;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.util.List;

public interface CollectionDao {

    @SqlUpdate("INSERT INTO collections (name, user_id) VALUES (:name, :user_id)")
    @GetGeneratedKeys
    long executeInsert(@Bind("name") String name, @Bind("user_id") Long userId);

    default Collection insert(String name, Long userId) throws CollectionExistsException {
        Long id;
        try {
            id = executeInsert(name, userId);
        }
        catch (UnableToExecuteStatementException e) {
            String message = e.getMessage();
            if (message.contains("duplicate key value violates unique constraint \"collections_user_id_name_key\"")) {
                throw new CollectionExistsException(e);
            }
            else {
                throw e;
            }
        }
        return ImmutableCollection
                .builder()
                .id(id)
                .name(name)
                .userId(userId)
                .build();
    }

    @SqlQuery("SELECT * FROM collections WHERE id=:id")
    @UseRowMapper(CollectionMapper.class)
    ImmutableCollection executeFindById(@Bind("id") Long id);

    default Collection findById(Long id) {
        return executeFindById(id);
    }

    @SqlQuery("SELECT * FROM collections;")
    @UseRowMapper(CollectionMapper.class)
    List<Collection> findAll();

    @SqlQuery("SELECT * FROM collections WHERE user_id=:id;")
    @UseRowMapper(CollectionMapper.class)
    List<Collection> findAllWithUserId(@Bind("id") Long id);
}
