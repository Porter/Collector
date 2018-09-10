package com.porter.collector.db;

import com.porter.collector.model.*;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.util.List;

public interface GoalDao {

    @SqlUpdate("INSERT INTO goals (name, user_id, report_id, target, indicator)" +
            " VALUES (:name, :userId, :reportId, :target, :indicator);")
    @GetGeneratedKeys
    long executeInsert(@Bind("name") String name,
                       @Bind("userId") long userId,
                       @Bind("reportId") long reportId,
                       @Bind("target") float target,
                       @Bind("indicator") int indicator);


    default Goal insert(String name, long userId, long reportId, float target, Indicator indicator)
            throws IllegalAccessException {
        if (confirmUserOwnsReport(reportId, userId) == null) {
            throw new IllegalAccessException("You can no longer modify that report");
        }
        Long id = executeInsert(name, userId, reportId, target, indicator.ordinal());
        return ImmutableGoal.builder()
                .id(id)
                .name(name)
                .reportId(reportId)
                .userId(userId)
                .target(target)
                .indicator(indicator)
                .build();
    }

    @SqlQuery("SELECT id FROM reports WHERE id=:id AND user_id=:userId")
    Long confirmUserOwnsReport(@Bind("id") long reportId, @Bind("userId") long userId);


    @SqlQuery("SELECT * FROM goals WHERE id=:id")
    @UseRowMapper(GoalsMapper.class)
    Goal findById(@Bind("id") Long id);

    @SqlQuery("SELECT * FROM goals")
    @UseRowMapper(GoalsMapper.class)
    List<Goal> findAll();

    @SqlQuery("SELECT * FROM goals WHERE user_id=:user_id")
    @UseRowMapper(GoalsMapper.class)
    List<Goal> findAllFromUser(@Bind("user_id") Long user_id);
}
