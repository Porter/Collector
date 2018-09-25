package com.porter.collector.db;

import com.porter.collector.exception.SignUpException;
import com.porter.collector.model.Category;
import com.porter.collector.model.CategoryMapper;
import com.porter.collector.model.ImmutableCategory;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

public interface CategoryDao {

    @SqlUpdate("INSERT INTO categories (name, collection_id) VALUES (:name, :collectionId)")
    @GetGeneratedKeys
    long executeInsert(@Bind("name") String name,
                                @Bind("collectionId") Long collectionId);

    default Category insert(String name, Long collectionId) throws SignUpException {
        long id = executeInsert(name, collectionId);

        return ImmutableCategory
                .builder()
                .id(id)
                .name(name)
                .collectionId(collectionId)
                .build();
    }


    @SqlQuery("SELECT * FROM categories WHERE id=:id")
    @UseRowMapper(CategoryMapper.class)
    Category findById(@Bind("id") Long id);
}
