package com.admiral.base;


import com.admiral.base.db.ADConnection;
import com.admiral.base.util.Ini;

public class Admiral {


    public static synchronized boolean startup(boolean isClient){
        Ini.setClient(isClient);
        Ini.loadProperties(false);

        ADConnection cc = ADConnection.get();
    }

    public static void main(String[] args) {

    }
}
