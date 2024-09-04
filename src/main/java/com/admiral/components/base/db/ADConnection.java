package com.admiral.components.base.db;

import com.admiral.components.base.util.Ini;
import com.admiral.components.base.util.secure.SecureEngine;

import javax.sql.DataSource;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ADConnection {
    private static final Logger log = Logger.getLogger(ADConnection.class.getName());

    private static ADConnection ad_cc = null;
    private String	dbName;
    private String	dbType;
    private String	dbPort;
    private String	dbHost;
    private String  dbUser = "adempiere";
    private String  dbPass = "adempiere";

    private GeneralDatabase ad_db = null;
    private DataSource ad_ds = null;

    public ADConnection(){}

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
            }
        }
        return ad_cc;
    }

    public void setDbName(String dbName){
        this.dbName = dbName;
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
    }

    public String getDbHost(){
        return dbHost;
    }

    public void setDbUser(String dbUser){
        this.dbUser = dbUser;
    }

    public String getDbUser(){
        return dbUser;
    }

    public void setDbPass(String dbPass){
        this.dbPass = dbPass;
    }

    public String getDbPass(){
        return dbPass;
    }

    public String getDbType(){
        return dbType;
    }

    public void setDbType(String dbType){
        for(int i = 0; i < Database.DATABASE_NAMES.length; i++){
            if(dbType.contains(Database.DATABASE_NAMES[i])){
                this.dbType = dbType;
                break;
            }
        }
    }
}
