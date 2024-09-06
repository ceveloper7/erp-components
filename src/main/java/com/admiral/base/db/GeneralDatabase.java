package com.admiral.base.db;

import java.sql.Driver;
import java.sql.SQLException;

public interface GeneralDatabase {
    public Driver getDriver() throws SQLException;
}
