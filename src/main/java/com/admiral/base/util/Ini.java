package com.admiral.base.util;

import com.admiral.base.util.secure.SecureEngine;
import com.admiral.base.util.secure.SecureInterface;
import com.admiral.components.base.util.secure.*;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Ini implements Serializable {
    private static Logger log = Logger.getLogger(Ini.class.getName());
    private static final long serialVersionUID = 3666529972922769528L;

    private static final String ADMIRAL_PROPERTY_FILE = "admiral.properties";
    private static final String ADMIRAL_HOME = "ADMIRAL_HOME";
    private static final String ENV_PREFIX = "env.";
    public static final String P_CONNECTION = "Connection";

    private static boolean s_client = true;
    private static boolean isLoaded = false;
    private static String propertyFileName = null;

    private static Properties admiralProperties = new Properties();

    // modo client?
    public static boolean isClient(){
        return s_client;
    }

    public static void setClient(boolean client){
        if(log != null){
            return;
        }
        s_client = client;
    }

    public static String getAdmiralHome(){
        String env = System.getProperty(ENV_PREFIX + ADMIRAL_HOME);
        if(env == null){
            env = System.getProperty(ADMIRAL_HOME);
        }
        if(env == null || "".equals(env)){
            env = File.separator + "Admiral";
        }
        return env;
    }

    public static String getAdmiralPropertyFileName(boolean userHome){
        if(System.getProperty("PropertyFile") != null){
            return System.getProperty("PropertyFile");
        }
        String base = null;
        if(userHome && s_client) {
            base = System.getProperty("user.home");
        }
        // server
        if((!s_client && base == null) || base.length() == 0){
            String home = getAdmiralHome();
            if(home != null){
                base = home;
            }
            if(base != null && !base.endsWith(File.separator)){
                base += File.separator;
            }
            if(base == null) {
                base = "";
            }
        }
        return base + ADMIRAL_PROPERTY_FILE;
    }

    public static void saveProperties(boolean userHome){
        String fileName = getAdmiralPropertyFileName(userHome);
        try{
            File f = new File(fileName);
            f.getAbsoluteFile().getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(f);
            admiralProperties.store(fos, "Admiral Properties");
            fos.flush();
            fos.close();
        }
        catch(Exception e){
            log.log(Level.SEVERE," Cannot save properties to " + fileName + " - " + e.toString());
        }
    }

    public static void loadProperties(boolean reload){
        if(reload || admiralProperties.isEmpty()){
            loadProperties(getAdmiralPropertyFileName(s_client));
        }
    }

    // load init app parameters from properties file
    public static boolean loadProperties(String fileName){
        boolean loaded = true;
        boolean firstTime = false;
        admiralProperties = new Properties();
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(fileName);
            admiralProperties.load(fis);
            fis.close();
        }
        catch(FileNotFoundException e){
            log.warning(fileName + " not found");
            loaded = false;
        }
        catch(Exception e){
            log.log(Level.SEVERE, fileName + " - " + e.toString());
            loaded = false;
        }
        catch (Throwable e){
            log.log(Level.SEVERE, fileName + " - " + e.toString());
            loaded = false;
        }

        if(!loaded || firstTime){
            saveProperties(true);
        }

        loaded = true;
        log.info(fileName + " - " + admiralProperties.size());
        propertyFileName = fileName;

        return firstTime;
    }

    public static String getProperty(String key){
        if (key == null)
            return "";
        String retStr = admiralProperties.getProperty(key, "");
        if (retStr == null || retStr.length() == 0)
            return "";
        //
        String value = SecureEngine.decrypt(retStr);
        if (value == null)
            return "";
        return value;
    }

    public static void setProperty(String key, String value){
        if(admiralProperties == null)
            admiralProperties = new Properties();
        if(!isClient()){
            admiralProperties.setProperty(key, SecureInterface.CLEARVALUE_START + value + SecureInterface.CLEARVALUE_END);
        }
        else{
            if(value == null){
                admiralProperties.setProperty(key, "");
            }
            else{
                String eValue = SecureEngine.encrypt(value);
                if(eValue == null){
                    admiralProperties.setProperty(key, "");
                }
                else{
                    admiralProperties.setProperty(key, eValue);
                }
            }
        }
    }
}
