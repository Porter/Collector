package com.porter.collector.db;

import com.porter.collector.model.*;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.util.List;

public interface ReportDao {

    @SqlUpdate("INSERT INTO reports (user_id, name, formula) VALUES (:userId, :name, :formula)")
    @GetGeneratedKeys
    long executeInsert(@Bind("userId") long userId, @Bind("name") String name, @Bind("formula") String formula);

    default Report insert(SimpleUser user, String name, String formula) {
        long id = executeInsert(user.id(), name, formula);

        return ImmutableReport
                .builder()
                .id(id)
                .userId(user.id())
                .name(name)
                .formula(formula)
                .build();
    }

    @SqlQuery("SELECT * FROM reports WHERE user_id=:userId")
    @UseRowMapper(ReportsMapper.class)
    List<Report> findAllFromUser(@Bind("userId") Long id);

    @SqlQuery("SELECT * FROM reports WHERE id=:id")
    @UseRowMapper(ReportsMapper.class)
    Report findById(@Bind("id") Long id);

}
