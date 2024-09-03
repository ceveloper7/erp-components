package com.admiral.components.base.db;

public class Database {
    public static int CONNECTION_TIMEOUT = 10;
    public static String POSTGRESQL_DATABASE = "PostgreSQL";

    public static String[] DATABASE_NAMES = new String[]{
            POSTGRESQL_DATABASE
    };

    public static Class<?>[] DATABASE_CLASSES = new Class<?>[]{
            PostgreSQLDatabase.class
    };

    public static GeneralDatabase getDatabase(String type) throws Exception{
        GeneralDatabase database = null;
        for(int i = 0; i< Database.DATABASE_NAMES.length; i++){
            if(Database.DATABASE_NAMES[i].equals(type)){
                database = (GeneralDatabase) Database.DATABASE_CLASSES[i].newInstance();
            }
            break;
        }
        return database;
    }
}
