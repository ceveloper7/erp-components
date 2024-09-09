package com.admiral.base.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

public interface AdmiralDatabase {
    public static final int LOCK_TIMEOUT = 60;
    public static final int CMD_CREATE_USER = 0;
    public static final int CMD_CREATE_DATABASE = 1;
    public static final int CMD_DELETE_DATABASE = 2;

    public static final String DEFAULT_CONNECTION_TEST_QUERY = "SELECT version FROM ad_system";

    public Driver getDriver() throws SQLException;

    /**
     * 	Close
     */
    public void close();

    public String getName();

    public DataSource getDataSource(ADConnection connection);

    /**
     * 	Get connection from Connection Pool
     *	@param connection info
     *  @param autoCommit true if autocommit connection
     *  @param transactionIsolation Connection transaction level
     *	@return connection or null
     *  @throws Exception
     */
    public Connection getCachedConnection(ADConnection connection,
                                            boolean autoCommit, int transactionIsolation) throws Exception;

    /**
     *  Get Database Connection String
     *  @param connection Connection Descriptor
     *  @return connection String
     */
    public String getConnectionURL(ADConnection connection);

}
