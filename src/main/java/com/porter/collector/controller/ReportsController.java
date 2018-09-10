package com.porter.collector.controller;

import com.porter.collector.db.ReportDao;
import com.porter.collector.model.Report;
import com.porter.collector.model.SimpleUser;
import com.porter.collector.parser.Parser;

import java.text.ParseException;
import java.util.List;

public class ReportsController {

    private final ReportDao reportDao;

    public ReportsController(ReportDao reportDao) {
        this.reportDao = reportDao;
    }

    public Report create(SimpleUser user, String name, String formula) throws ParseException {
        Parser parser = new Parser();
        if (parser.isValid(formula)) {
            String value = new Parser().parse(formula).execute(user).stringify();
            return reportDao.insert(user, name, formula, value);
        }
        throw new ParseException(formula + " is not valid", 0);
    }

    public List<Report> getAll(SimpleUser user) {
        return reportDao.updateAndGetAll(user);
    }
}
