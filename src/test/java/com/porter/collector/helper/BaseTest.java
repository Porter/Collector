package com.porter.collector.helper;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.Before;

public class BaseTest {

    private Jdbi jdbi;

    private void initJDBI() {
        jdbi = Jdbi.create("jdbc:postgresql://localhost/collector_test", "postgres", "postgres");
        jdbi.installPlugin(new SqlObjectPlugin());
    }

    protected Jdbi getJdbi() {
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
