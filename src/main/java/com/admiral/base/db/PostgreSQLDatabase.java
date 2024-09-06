package com.admiral.base.db;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLDatabase implements GeneralDatabase{
    public static final String DRIVER = "org.postgresql.Driver";
    private org.postgresql.Driver s_driver;
    public static final int DEFAULT_PORT = 5432;

    public PostgreSQLDatabase() {}

    @Override
    public Driver getDriver() throws SQLException {
        if(s_driver == null) {
            s_driver = new org.postgresql.Driver();
            DriverManager.registerDriver(s_driver);
            DriverManager.setLoginTimeout(Database.CONNECTION_TIMEOUT);
        }
        return s_driver;
    }
}
