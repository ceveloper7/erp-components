package com.admiral.base;


import com.admiral.base.db.ADConnection;
import com.admiral.base.db.DataBase;
import com.admiral.base.util.Ini;

import java.util.logging.Logger;

public class Admiral {
    private static final Logger log = Logger.getLogger(Admiral.class.getName());

    public static boolean startupEnvironment(boolean isClient){
        startup(isClient);
        if(!DataBase.isConnected()){
            log.severe("No Database");
            return false;
        }

        return true;
    }

    public static synchronized boolean startup(boolean isClient){
        Ini.setClient(isClient);
        Ini.loadProperties(false);

        ADConnection cc = ADConnection.get();
        DataBase.setDBTarget(cc);

        if(isClient){
            return false;
        }

        return startupEnvironment(isClient);
    }

    public static void main(String[] args) {
        startup(true);
    }
}
