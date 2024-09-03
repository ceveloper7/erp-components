package com.admiral.components.base.db;

import java.sql.Driver;
import java.sql.SQLException;

public class PostgreSQLDatabase implements GeneralDatabase{
    public static final String DRIVER = "org.postgresql.Driver";
    public static final int DEFAULT_PORT = 5432;

    public PostgreSQLDatabase() {}

    @Override
    public Driver getDriver() throws SQLException {
        return null;
    }
}
