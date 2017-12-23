package com.porter.collector.db;

import com.porter.collector.errors.SignUpException;
import com.porter.collector.model.Category;
import com.porter.collector.model.CategoryMapper;
import com.porter.collector.model.ImmutableCategory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public abstract class CategoryDao {

    @SqlUpdate("INSERT INTO categories (name, collection_id) VALUES (:name, :collectionId)")
    @GetGeneratedKeys
    abstract long executeInsert(@Bind("name") String name,
                                @Bind("collectionId") Long collectionId);

    public Category insert(String name, Long collectionId) throws SignUpException {
        long id = executeInsert(name, collectionId);

        return ImmutableCategory
                .builder()
                .id(id)
                .name(name)
                .collectionId(collectionId)
                .build();
    }


    @SqlQuery("SELECT * FROM categories WHERE id=:id")
    @Mapper(CategoryMapper.class)
    public abstract Category findById(@Bind("id") Long id);
}
