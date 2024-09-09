package com.admiral.base.db;

import com.admiral.base.util.Ini;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgreSQLDatabase implements AdmiralDatabase {
    private static final Logger log = Logger.getLogger(PostgreSQLDatabase.class.getName());

    public static final String DRIVER = "org.postgresql.Driver";
    private org.postgresql.Driver ad_driver;
    public static final int DEFAULT_PORT = 5432;

    private String ad_connection;
    private String ad_dbName = null;
    private String ad_userName = null;
    private String ad_connectionUrl;

    /** Data Source	Long Running 	*/
    private DataSource datasourceLongRunning = null;
    private PGSimpleDataSource m_ds = null;

    public PostgreSQLDatabase() {}

    public String getConnectionURL (ADConnection connection)
    {
        //  jdbc:postgresql://hostname:portnumber/databasename?encoding=UNICODE
        StringBuffer sb = new StringBuffer("jdbc:postgresql:");
        sb.append("//").append(connection.getDbHost())
                .append(":").append(connection.getDbPort())
                .append("/").append(connection.getDbName())
                .append("?encoding=UNICODE");
        ad_connection = sb.toString();
        return ad_connection;
    }   //  getConnectionString

    public Connection getCachedConnection(ADConnection connection, boolean autoCommit, int transactionIsolation) throws Exception{
        if(m_ds == null){
            getDataSource(connection);
        }
        Connection localConnection = m_ds.getConnection();
        if(localConnection != null){
            localConnection.setAutoCommit(autoCommit);
            localConnection.setTransactionIsolation(transactionIsolation);
        }
        return localConnection;
    }

    @Override
    public Driver getDriver() throws SQLException {
        if(ad_driver == null) {
            ad_driver = new org.postgresql.Driver();
            DriverManager.registerDriver(ad_driver);
            DriverManager.setLoginTimeout(DataBasesSupported.CONNECTION_TIMEOUT);
        }
        return ad_driver;
    }

    public DataSource getDataSource(ADConnection connection) {
        if (m_ds != null) {
            return m_ds;
        }

        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName(connection.getDbHost());
        ds.setDatabaseName(connection.getDbName());
        ds.setUser(connection.getDbUser());
        ds.setPassword(connection.getDbPass());
        ds.setPortNumber(Integer.parseInt(connection.getDbPort()));

        ds.setSocketTimeout(0);
        m_ds	= ds;

        return m_ds;

//        if(datasourceLongRunning != null) {
//            return datasourceLongRunning;
//        }
//        try{
//            if(Ini.isClient()){
//                log.warning("Config Hikari Connection Pool Datasource");
//                HikariConfig config = new HikariConfig();
//                config.setDriverClassName(DRIVER);
//                config.setJdbcUrl(getConnectionURL(connection));
//                config.setUsername(connection.getDbUser());
//                config.setPassword(connection.getDbPass());
//                config.setConnectionTestQuery(DEFAULT_CONNECTION_TEST_QUERY);
//                config.setIdleTimeout(0);
//                config.setMinimumIdle(15);
//                config.setMaximumPoolSize(150);
//                config.setPoolName("AdempiereDS");
//                config.addDataSourceProperty( "cachePrepStmts" , "true" );
//                config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
//                config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
//                datasourceLongRunning = new HikariDataSource(config);;
//                log.warning("Starting Client Hikari Connection Pool");
//            }
//        }
//        catch(Exception exception){
//            datasourceLongRunning = null;
//            log.log(Level.SEVERE, "Application Server does not exist, no is possible to initialize the initialise Hikari Connection Pool", exception);
//            exception.printStackTrace();
//        }
//        return datasourceLongRunning;
    }

    public String toString() {

        StringBuffer	sb	= new StringBuffer("DB_PostgreSQL[");

        sb.append(ad_connection).append("]");

        return sb.toString();

    }		// toString

    @Override
    public synchronized void close() {
        log.fine(toString());
        if (datasourceLongRunning != null)
        {
            try
            {
                datasourceLongRunning.getConnection().close(); //m_ds.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        datasourceLongRunning = null;
    }

    @Override
    public String getName() {
        return "";
    }
}
