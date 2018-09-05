package com.porter.collector.db;

import com.google.common.collect.ImmutableList;
import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ReportDaoTest extends BaseTest {

    private UserDao userDao;
    private ReportDao reportDao;

    @Before
    public void getDao() {
        userDao = getJdbi().onDemand(UserDao.class);
        reportDao = getJdbi().onDemand(ReportDao.class);
    }

    @Test
    public void insert_findById() throws Exception {
        UserWithPassword user = userDao.insert("a@g.com", "name", "pass");
        Report report = reportDao.insert(user, "myReport", "formula");
        Report expected = reportDao.findById(report.id());

        assertEquals(expected, report);
    }


    @Test
    public void findAllFromUser() {
        UserWithPassword user1 = userDao.insert("a@g.com", "name", "pass");
        UserWithPassword user2 = userDao.insert("a2@g.com", "name2", "pass");
        Report report1 = reportDao.insert(user1, "myReport", "formula");
        Report report2 = reportDao.insert(user1, "myReport", "formula");
        Report report3 = reportDao.insert(user2, "myReport", "formula");

        List<Report> reports1 = ImmutableList.of(report1, report2);
        List<Report> reports2 = ImmutableList.of(report3);

        assertEquals(reports1, reportDao.findAllFromUser(user1.id()));
        assertEquals(reports2, reportDao.findAllFromUser(user2.id()));
    }
}