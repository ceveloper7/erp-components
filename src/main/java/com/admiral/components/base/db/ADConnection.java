package com.admiral.components.base.db;

public class ADConnection {

    private String	dbName;
    private String	dbType;
    private String	dbPort;
    private String	dbHost;
    private String  dbUser = "adempiere";
    private String  dbPass = "adempiere";

    public ADConnection(){}

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
