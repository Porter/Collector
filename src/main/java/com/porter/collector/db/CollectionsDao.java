package com.porter.collector.db;

import com.porter.collector.db.ImmutableCollection;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public abstract class CollectionsDao {

    @SqlUpdate("INSERT INTO collections (name, user_id) VALUES (:name, :user_id)")
    @GetGeneratedKeys
    abstract long executeInsert(@Bind("name") String name, @Bind("user_id") Long userId);

    public Collection insert(String name, User user) {
        Long id = executeInsert(name, user.id());
        return ImmutableCollection
                .builder()
                .id(id)
                .name(name)
                .user(user)
                .userId(user.id())
                .build();
    }

    @SqlQuery("SELECT * FROM collections WHERE id=:id")
    @Mapper(CollectionsMapper.class)
    abstract ImmutableCollection executeFindById(@Bind("id") Long id);

    @SqlQuery("SELECT * FROM users WHERE id=:id")
    @Mapper(UsersMapper.class)
    abstract User executeFindUserById(@Bind("id") Long id);

    public Collection findById(Long id) {
        ImmutableCollection collection = executeFindById(id);

        return collection.withUser(executeFindUserById(collection.userId()));
    }
}
