package com.porter.collector.db;

import com.porter.collector.model.*;
import com.porter.collector.parser.Parser;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.util.List;
import java.util.stream.Collectors;

public interface ReportDao {

    @SqlUpdate("INSERT INTO reports (user_id, name, formula, value) VALUES (:userId, :name, :formula, :value)")
    @GetGeneratedKeys
    long executeInsert(
            @Bind("userId") long userId,
            @Bind("name") String name,
            @Bind("formula") String formula,
            @Bind("value") String value);

    default Report insert(SimpleUser user, String name, String formula) {
        return insert(user, name, formula, null);
    }

    @SqlQuery("SELECT * FROM reports WHERE user_id=:userId")
    @UseRowMapper(ReportsMapper.class)
    List<Report> findAllFromUser(@Bind("userId") Long id);

    @SqlQuery("SELECT * FROM reports WHERE id=:id")
    @UseRowMapper(ReportsMapper.class)
    Report findById(@Bind("id") Long id);

    default Report insert(SimpleUser user, String name, String formula, String value) {
        long id = executeInsert(user.id(), name, formula, value);

        return ImmutableReport
                .builder()
                .id(id)
                .userId(user.id())
                .name(name)
                .formula(formula)
                .value(value)
                .build();
    }

    default List<Report> updateAndGetAll(SimpleUser user) {
        List<Report> reports = findAllFromUser(user.id());
        reports = reports.stream()
                .map(r -> ImmutableReport.copyOf(r).withValue(
                        new Parser().parse(r.formula()).execute(user).stringify()
                ))
                .collect(Collectors.toList());

        reports.forEach(r -> _updateValue(r.id(), r.value()));
        return reports;
    }

    @SqlUpdate("UPDATE reports SET value=:value WHERE id=:id")
    void _updateValue(@Bind("id") long id, @Bind("value") String value);
}
