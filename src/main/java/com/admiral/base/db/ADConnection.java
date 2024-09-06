package com.admiral.base.db;

import com.admiral.base.util.secure.SecureEngine;
import com.admiral.base.util.Ini;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ADConnection {
    private static final Logger log = Logger.getLogger(ADConnection.class.getName());

    private static ADConnection ad_cc = null;
    // connection name
    private String adName = "Admiral";
    private String	dbName;
    private String	dbType;
    private String	dbPort;
    private String	dbHost;
    private String  dbUser = "adempiere";
    private String  dbPass = "adempiere";

    /** Info                */
    private String[] 	m_info = new String[2];

    private Exception ad_dbException = null;
    private AdmiralDatabase ad_db = null;
    private boolean ad_okDB = false;
    private DataSource ad_ds = null;

    protected ADConnection(){}

    public boolean isDatabaseOK ()
    {
        return ad_okDB;
    } 	//  isDatabaseOK

    protected void setName(){
        this.adName = toString();
    }

    public void getName(String name){
        adName = name;
    }

    public void setDbName(String dbName){
        this.dbName = dbName;
        this.adName = toString();
        ad_okDB = false;
    }

    public String getDbName(){
        return dbName;
    }

    public void setDbPort(String dbPort){
        this.dbPort = dbPort;
    }

    public String getDbPort(){
        return dbPort;
    }

    public void setDbHost(String dbHost){
        this.dbHost = dbHost;
        this.adName = toString();
        ad_okDB = false;
    }

    public String getDbHost(){
        return dbHost;
    }

    public void setDbUser(String dbUser){
        this.dbUser = dbUser;
        this.adName = toString();
        ad_okDB = false;
    }

    public String getDbUser(){
        return dbUser;
    }

    public void setDbPass(String dbPass){
        this.dbPass = dbPass;
        ad_okDB = false;
    }

    public String getDbPass(){
        return dbPass;
    }

    public String getDbType(){
        return dbType;
    }

    public void setDbType(String dbType){
        for(int i = 0; i < DataBasesSupported.DATABASE_NAMES.length; i++){
            if(dbType.contains(DataBasesSupported.DATABASE_NAMES[i])){
                this.dbType = dbType;
                break;
            }
        }
    }

    public static ADConnection get(){
        return get(null);
    }

    private String unescape(String value) {
        value = value.replace("&eq;", "=");
        value = value.replace("&comma;", ",");
        return value;
    }

    private void setAttributes(String attributes){
        try {
            attributes = attributes.substring(attributes.indexOf("[") + 1, attributes.length() - 1);
            String[] pairs = attributes.split("[,]");
            for(String pair : pairs){
                String[] pairComponents = pair.split("[=]");
                String key = pairComponents[0];
                String value = pairComponents.length == 2 ? unescape(pairComponents[1]) : "";
                if("DBName".equalsIgnoreCase(key)){
                    setDbName(value);
                } else if ("type".equalsIgnoreCase(key)) {
                    setDbType(value);
                }else if("DBPort".equalsIgnoreCase(key)){
                    setDbPort(value);
                }else if("DBHost".equalsIgnoreCase(key)){
                    setDbHost(value);
                }else if("UID".equalsIgnoreCase(key)){
                    setDbUser(value);
                }else if("PWD".equalsIgnoreCase(key)){
                    setDbPass(value);
                }
            }
        }
        catch(Exception e){
            log.log(Level.SEVERE, attributes + " - " + e.toString());
        }
    }

    public static ADConnection get(String ad_apps_host){
        if(ad_cc == null){
            String attributes = Ini.getProperty(Ini.P_CONNECTION);
            if(attributes == null || attributes.isEmpty()){
                attributes = SecureEngine.decrypt(System.getProperty(Ini.P_CONNECTION));
            }

            if(attributes != null && !attributes.isEmpty()){
                ad_cc = new ADConnection();
                ad_cc.setAttributes(attributes);
                log.fine(ad_cc.toString());
                Ini.setProperty(Ini.P_CONNECTION, ad_cc.toString());
                // TODO: Ini.saveProperties(Ini.isClient());
            }else{
                ad_cc = new ADConnection();
                ad_cc.setAttributes(attributes);
            }
            log.fine(ad_cc.toString());
        }
        return ad_cc;
    }



    @Override
    public Object clone() throws CloneNotSupportedException {
        ADConnection c = (ADConnection)super.clone();
        String[] info = new String[2];
        info[0] = m_info[0];
        info[1] = m_info[1];
        c.m_info = info;
        return c;
    }

    public AdmiralDatabase getDatabase(){
        if((ad_db != null) && !ad_db.getName().equals(dbType)){
            ad_db = null;
        }

        if(ad_db == null){
            try{
                for(int i = 0; i < DataBasesSupported.DATABASE_NAMES.length; i++){
                    if(DataBasesSupported.DATABASE_NAMES[i].equals(dbType)){
                        ad_db = (AdmiralDatabase) DataBasesSupported.DATABASE_CLASSES[i].newInstance();
                        break;
                    }
                }
                if(ad_db != null){
                    ad_db.getDataSource(this);
                }
            }
            catch(Exception e){
                log.severe(e.toString());
            }
        }
        return ad_db;
    }

    public boolean isDataSource(){
        return ad_ds != null;
    }

    public void readInfo(Connection conn) throws SQLException {
        DatabaseMetaData dbmd = conn.getMetaData();
        m_info[0] = "Database=" + dbmd.getDatabaseProductName() + " - " + dbmd.getDatabaseProductVersion();
        m_info[0] = m_info[0].replace('\n', ' ');
        m_info[1] = "Drive =" + dbmd.getDriverName() + " - " + dbmd.getDriverVersion();
        if(isDataSource()){
            m_info[1] += " - via Datasouurce";
        }
        m_info[1] = m_info[1].replace('\n', ' ');
        log.config(m_info[0] + " - " + m_info[1]);
    }

    public Exception testDatabase(){
        if(ad_ds != null){
            getDatabase().close();
        }
        ad_ds = null;
        setDataSource();

        Connection conn = getConnection(true, Connection.TRANSACTION_READ_COMMITTED);
        if(conn != null){
            try{
                readInfo(conn);
                conn.close();
            }
            catch(Exception ex){
                log.log(Level.SEVERE, ex.toString());
                return ex;
            }
        }
        return ad_dbException;
    }

    public boolean setDataSource(){
        if((ad_ds == null) && Ini.isClient()){
            if(getDatabase() != null){
                ad_ds = getDatabase().getDataSource(this);
            }
        }
        return ad_ds != null;
    }

    public boolean setDataSource(DataSource ds){
        if(ds == null && ad_ds != null){
            getDatabase().close();
        }
        ad_ds = ds;
        return ad_ds != null;
    }

    /**
     *      Get Transaction Isolation Info
     *      @param transactionIsolation trx iso
     *      @return clear test
     */
    public static String getTransactionIsolationInfo(int transactionIsolation) {

        if (transactionIsolation == Connection.TRANSACTION_NONE) {
            return "NONE";
        }

        if (transactionIsolation == Connection.TRANSACTION_READ_COMMITTED) {
            return "READ_COMMITTED";
        }

        if (transactionIsolation == Connection.TRANSACTION_READ_UNCOMMITTED) {
            return "READ_UNCOMMITTED";
        }

        if (transactionIsolation == Connection.TRANSACTION_REPEATABLE_READ) {
            return "REPEATABLE_READ";
        }

        if (transactionIsolation == Connection.TRANSACTION_READ_COMMITTED) {
            return "SERIALIZABLE";
        }

        return "<?" + transactionIsolation + "?>";

    }		// getTransactionIsolationInfo

    public String getConnectionURL(){
        getDatabase();
        if(ad_db != null){
            return ad_db.getConnectionURL(this);
        }else{
            return "";
        }
    }

    public Connection getConnection(boolean autoCommit, int transactionIsolation){
        Connection connection = null;
        ad_dbException = null;
        ad_okDB = false;

        getDatabase();
        if(ad_db == null){
            ad_dbException = new IllegalStateException("No database connector");
            return null;
        }

        try{
            Exception ee = null;
            try{
                connection = ad_db.getFromConnectionPool(this, autoCommit, transactionIsolation);
            }
            catch(Exception exception){
                log.severe(exception.getMessage());
                ee = exception;
            }
            //	Verify Connection
            if (connection != null)
            {
                if (connection.getTransactionIsolation() != transactionIsolation)
                    connection.setTransactionIsolation (transactionIsolation);
                if (connection.getAutoCommit() != autoCommit)
                    connection.setAutoCommit (autoCommit);
                ad_okDB = true;
            }
        }
        catch (UnsatisfiedLinkError ule){
            String msg = ule.getLocalizedMessage()
                    + " -> Did you set the LD_LIBRARY_PATH ? - " + getConnectionURL();
            ad_dbException = new Exception(msg);
            log.severe(msg);
        }
        catch (SQLException ex){
            if(connection == null){
                log.log(Level.SEVERE, getConnectionURL() + ", (1) AutoCommit=" + autoCommit + ",TrxIso=" + getTransactionIsolationInfo(transactionIsolation)

                        // + " (" + getDbUid() + "/" + getDbPwd() + ")"
                        + " - " + ex.getMessage());
            }else{
                try{
                    log.severe(getConnectionURL() + ", (2) AutoCommit=" + connection.getAutoCommit() + "->" + autoCommit + ", TrxIso=" + getTransactionIsolationInfo(connection.getTransactionIsolation()) + "->" + getTransactionIsolationInfo(transactionIsolation)

                            // + " (" + getDbUid() + "/" + getDbPwd() + ")"
                            + " - " + ex.getMessage());
                }
                catch (Exception ee){
                    log.severe(getConnectionURL() + ", (3) AutoCommit=" + autoCommit + ", TrxIso=" + getTransactionIsolationInfo(transactionIsolation)

                            // + " (" + getDbUid() + "/" + getDbPwd() + ")"
                            + " - " + ex.getMessage());
                }
            }
        }
        catch(Exception ex){
            ad_dbException = ex;
            log.log(Level.SEVERE, getConnectionURL(), ex);
        }
        return connection;
    }
}
