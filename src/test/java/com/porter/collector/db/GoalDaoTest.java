package com.porter.collector.db;

import com.google.common.collect.ImmutableList;
import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;


public class GoalDaoTest extends BaseTest {

    private UserDao userDao;
    private ReportDao reportDao;
    private GoalDao goalDao;

    @Before
    public void getDAOs() {
        userDao = getJdbi().onDemand(UserDao.class);
        reportDao      = getJdbi().onDemand(ReportDao.class);
        goalDao        = getJdbi().onDemand(GoalDao.class);
    }


    @Test
    public void insert_findById() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Report report = reportDao.insert(user, "myReport", "sum(1, 1)");

        Long id = goalDao.insert("myGoal", user.id(), report.id(), 1.3f, Indicator.EQUAL_TO).id();

        Goal expected = ImmutableGoal
                .builder()
                .id(id)
                .name("myGoal")
                .reportId(report.id())
                .userId(user.id())
                .target(1.3f)
                .indicator(Indicator.EQUAL_TO)
                .build();

        Assert.assertEquals(expected, goalDao.findById(id));
    }

    @Test
    public void findAll() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Report report = reportDao.insert(user, "myReport", "sum(1, 1)");

        Goal goal1 = goalDao.insert("myGoal", user.id(), report.id(), 1.3f, Indicator.EQUAL_TO);
        Goal goal2 = goalDao.insert("myGoal2", user.id(), report.id(), 1.3f, Indicator.EQUAL_TO);
        Goal goal3 = goalDao.insert("myGoal3", user.id(), report.id(), 1.3f, Indicator.EQUAL_TO);

        List<Goal> expected = ImmutableList.of(goal1, goal2, goal3);

        Assert.assertEquals(expected, goalDao.findAll());
    }

    @Test
    public void findAllFromUser() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        UserWithPassword user2 = userDao.insert("a2@g.com", "name2", "pass");
        Report report = reportDao.insert(user, "myReport", "sum(1, 1)");
        Report report2 = reportDao.insert(user2, "myReport", "sum(1, 1)");

        Goal goal1 = goalDao.insert("myGoal", user.id(), report.id(), 1.3f, Indicator.EQUAL_TO);
        Goal goal2 = goalDao.insert("myGoal2", user2.id(), report2.id(), 1.3f, Indicator.EQUAL_TO);
        Goal goal3 = goalDao.insert("myGoal3", user2.id(), report2.id(), 1.3f, Indicator.EQUAL_TO);
        List<Goal> expected = ImmutableList.of(goal1);
        List<Goal> expected2 = ImmutableList.of(goal2, goal3);

        Assert.assertEquals(expected, goalDao.findAllFromUser(user.id()));
        Assert.assertEquals(expected2, goalDao.findAllFromUser(user2.id()));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void badUserId() throws Exception {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("java.lang.IllegalAccessException: You can no longer modify that report");
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Report report = reportDao.insert(user, "myReport", "sum(1, 1)");
        goalDao.insert("myGoal3", user.id() + 1, report.id(), 1.3f, Indicator.EQUAL_TO);
    }

    @Test
    public void incorrectUserId() throws Exception {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("java.lang.IllegalAccessException: You can no longer modify that report");
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        UserWithPassword user2 = userDao.insert("a2@g.com", "name2", "pass");
        Report report = reportDao.insert(user, "myReport", "sum(1, 1)");
        goalDao.insert("myGoal3", user2.id(), report.id(), 1.3f, Indicator.EQUAL_TO);
    }
}