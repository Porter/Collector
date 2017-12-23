package com.porter.collector.helper;

import org.junit.Before;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class BaseTest {

    private DBI jdbi;

    private void initJDBI() {
        jdbi = new DBI("jdbc:postgresql://localhost/collector_test", "postgres", "postgres");
    }

    protected DBI getJdbi() {
        if (jdbi == null) {
            initJDBI();
        }

        return jdbi;
    }


    @Before
    public void reset() {
        String[] tableNames = new String[]{"users", "collections"};

        try (Handle handle = getJdbi().open()) {
            for (String tableName : tableNames) {
                handle.execute("TRUNCATE " + tableName + " CASCADE");
            }
        }
    }
}
