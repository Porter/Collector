package com.porter.collector.controller;

import com.porter.collector.db.ReportDao;
import com.porter.collector.errors.SignUpException;
import com.porter.collector.model.Report;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.model.UserWithPassword;

import java.util.List;

public class ReportsController {

    private final ReportDao reportDao;

    public ReportsController(ReportDao reportDao) {
        this.reportDao = reportDao;
    }

    public Report create(SimpleUser user, String name, String formula) {
        return reportDao.insert(user, name, formula);
    }

    public List<Report> getAll(SimpleUser user) {
        return reportDao.findAllFromUser(user.id());
    }
}
