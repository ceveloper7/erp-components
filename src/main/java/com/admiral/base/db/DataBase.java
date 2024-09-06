package com.admiral.base.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public final class DataBase {
    private static final Logger log = Logger.getLogger(DataBase.class.getName());

    private static ADConnection s_cc = null;
    private static Object			s_ccLock = new Object();

    public static void setDBTarget(ADConnection cc){
        if (cc == null)
            throw new IllegalArgumentException("Connection is NULL");

        if (s_cc != null && s_cc.equals(cc))
            return;

        DataBase.closeTarget();

        synchronized(s_ccLock)
        {
            s_cc = cc;
        }

        s_cc.setDataSource();

        log.config(s_cc + " - DS=" + s_cc.isDataSource());
    }

    public static void closeTarget(){
        boolean closed = false;

        // ADConnection
        if(s_cc != null){
            closed = true;
            s_cc.setDataSource(null);
        }
        s_cc = null;
        if(closed){
            log.fine("DataBase closed");
        }
    }

    public static boolean isConnected()
    {
        return isConnected(true);
    }

    public static boolean isConnected(boolean createNew){
        if (s_cc == null) return false;

        boolean success = false;

        try{
            Connection conn = getConnectionRW(createNew);
            if(conn != null){
                conn.close();
            }
            success = (conn != null);
        }
        catch(SQLException e){
            success = false;
        }
        return success;
    }

    public static Connection getConnectionRW()
    {
        return getConnectionRW(true);
    }

    public static Connection getConnectionRW (boolean createNew)
    {
        return createConnection(true, false, Connection.TRANSACTION_READ_COMMITTED);
    }   //  getConnectionRW

    public static Connection createConnection(boolean autoCommit, boolean readOnly, int trxLevel){
        Connection conn = s_cc.getConnection(autoCommit, trxLevel);
        if(conn == null){
            throw new IllegalStateException("DB.getConnectionRO - @NoDBConnection@");
        }

        try {
            if (conn.getAutoCommit() != autoCommit)
            {
                throw new IllegalStateException("Failed to set the requested auto commit mode on connection. [autocommit=" + autoCommit +"]");
            }
        } catch (SQLException exception) {
            log.severe(exception.getMessage());
        }

        return conn;
    }

}
