package com.admiral.base.db;

public class DataBasesSupported {
    public static int CONNECTION_TIMEOUT = 10;
    public static String POSTGRESQL_DATABASE = "PostgreSQL";

    public static String[] DATABASE_NAMES = new String[]{
            POSTGRESQL_DATABASE
    };

    public static Class<?>[] DATABASE_CLASSES = new Class<?>[]{
            PostgreSQLDatabase.class
    };

    public static AdmiralDatabase getDatabase(String type) throws Exception{
        AdmiralDatabase database = null;
        for(int i = 0; i< DataBasesSupported.DATABASE_NAMES.length; i++){
            if(DataBasesSupported.DATABASE_NAMES[i].equals(type)){
                database = (AdmiralDatabase) DataBasesSupported.DATABASE_CLASSES[i].newInstance();
            }
            break;
        }
        return database;
    }
}
