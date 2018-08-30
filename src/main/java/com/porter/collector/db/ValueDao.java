package com.porter.collector.db;

import com.porter.collector.model.*;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

public interface ValueDao {

    @SqlUpdate("INSERT INTO values (value) VALUES (:value)")
    @GetGeneratedKeys
    long executeInsert(@Bind("value") String value);

    default Value insert(String value) {
        long id = executeInsert(value);

        return ImmutableValue
                .builder()
                .id(id)
                .value(value)
                .build();
    }


    @SqlQuery("SELECT * FROM values WHERE id=:id")
    @UseRowMapper(ValuesMapper.class)
    Value findById(@Bind("id") Long id);
}
