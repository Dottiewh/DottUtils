package mp.dottiewh.config;

import mp.dottiewh.DottUtils;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private static CustomConfig config;
    private static List<String> admins;
    private static List<String> whitelist;
    private static boolean whitelistStatus;



    //------config--------
    public static void configInit(){
        config = DottUtils.getRegisteredConfig();
        admins = config.getConfig().getStringList("adminlist");
        whitelist = config.getConfig().getStringList("whitelist");
        whitelistStatus = config.getConfig().getBoolean("whitelist_active");
    }
    public static void configReload(){
        DottUtils.initCustomConfig();
    }
    //------ADMINS--------
    public static boolean addAdmin(String newAdmin){ // boolean = Was successful?
        if (admins.contains(newAdmin)) return false;

        admins.add(newAdmin);
        config.getConfig().set("adminlist", admins);
        config.saveConfig();
        return true;
    }
    public static boolean removeAdmin(String oldAdmin){ // boolean = Was successful?
        if (!admins.contains(oldAdmin)) return false;

        for (String adminsito : new ArrayList<>(admins)) { // borrar repetidos en caso de
            if (adminsito.equalsIgnoreCase(oldAdmin)) {
                admins.remove(adminsito);
            }
        }

        //admins.remove(oldAdmin);
        config.getConfig().set("adminlist", admins);
        config.saveConfig();
        return true;
    }
    public static boolean containsAdmin(String name){
        if (name.equalsIgnoreCase("console")){
            return admins.contains("Console");
        }

        return admins.contains(name);
    }
    public static List<String> getAdminList(){
        return admins;
    }

    //-------WHITELIST---------
    public static boolean onWhitelist(){ // boolean = Was successful?
        if (getWhiteListStatus()) return false;

        config.getConfig().set("whitelist_active", true);
        config.saveConfig();
        whitelistStatus = true;
        return true;
    }
    public static boolean offWhitelist(){ // boolean = Was successful?
        if (!getWhiteListStatus()) return false;

        config.getConfig().set("whitelist_active", false);
        config.saveConfig();
        whitelistStatus = false;
        return true;
    }
    //-
    public static boolean addWhitelist(String newBlanco){ // boolean = Was successful?
        if (whitelist.contains(newBlanco)) return false;

        whitelist.add(newBlanco);
        config.getConfig().set("whitelist", whitelist);
        config.saveConfig();
        return true;
    }
    public static boolean removeWhitelist(String oldBlanco){ // boolean = Was successful?
        if (!whitelist.contains(oldBlanco)) return false;

        for (String blanco : new ArrayList<>(whitelist)) { // borrar repetidos en caso de
            if (blanco.equalsIgnoreCase(oldBlanco)) {
                whitelist.remove(blanco);
            }
        }

        //admins.remove(oldWhitelisted);
        config.getConfig().set("whitelist", whitelist);
        config.saveConfig();
        return true;
    }
    public static boolean containsWhitelist(String name){

        return whitelist.contains(name);
    }
    public static List<String> getWhitelist(){
        return whitelist;
    }
    public static boolean getWhiteListStatus(){
        return whitelistStatus;
    }
}
