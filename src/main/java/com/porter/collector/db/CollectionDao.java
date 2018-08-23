package com.porter.collector.db;

import com.porter.collector.errors.CollectionExistsException;
import com.porter.collector.model.*;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.List;

public abstract class CollectionDao {

    @SqlUpdate("INSERT INTO collections (name, user_id) VALUES (:name, :user_id)")
    @GetGeneratedKeys
    abstract long executeInsert(@Bind("name") String name, @Bind("user_id") Long userId);

    public Collection insert(String name, Long userId) throws CollectionExistsException {
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
    @Mapper(CollectionMapper.class)
    abstract ImmutableCollection executeFindById(@Bind("id") Long id);

    public Collection findById(Long id) {
        return executeFindById(id);
    }

    @SqlQuery("SELECT * FROM collections;")
    @Mapper(CollectionMapper.class)
    public abstract List<Collection> findAll();

    @SqlQuery("SELECT * FROM collections WHERE user_id=:id;")
    @Mapper(CollectionMapper.class)
    public abstract List<Collection> findAllWithUserId(@Bind("id") Long id);
}
