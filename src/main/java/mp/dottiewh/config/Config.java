package mp.dottiewh.config;

import mp.dottiewh.DottUtils;
import mp.dottiewh.commands.aliasCommands.AdminChat;
import mp.dottiewh.utils.U;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private static CustomConfig config;
    private static CustomConfig configLists;

    //private static CustomConfig regCmdConfig;

    private static List<String> admins;
    private static List<String> whitelist;
    private static boolean whitelistStatus;
    private static boolean maintenanceStatus;
    private static boolean pvpStatus;
    private static boolean noFallStatus;


    //----registered commands----

    /*public static boolean getRegCmdStatus(String cmd, boolean def){
        return getRegCmdStatus(regCmdConfig.getConfig(), cmd, def);
    }*/

    //------config--------
    public static void configInit(){
        config = DottUtils.getRegisteredConfig();
        configLists = DottUtils.getRegisteredConfigLists();

        AdminChat.acPrefixReload();
        admins = configLists.getConfig().getStringList("adminlist");
        whitelist = configLists.getConfig().getStringList("whitelist");
        whitelistStatus = config.getConfig().getBoolean("whitelist_active", false);
        maintenanceStatus = config.getConfig().getBoolean("maintenance_active", false);
        pvpStatus = config.getConfig().getBoolean("pvp", true);
        noFallStatus = config.getConfig().getBoolean("no_fall", false);
    }
    public static void configReload(){
        DottUtils.initCustomConfig();
    }
    //--getters--
    public static int getInt(String path){
        return DottUtils.ymlConfig.getConfig().getInt(path, -1);
    }
    public static int getInt(String path, int def){
        int toGive = DottUtils.ymlConfig.getConfig().getInt(path, -1);
        if (toGive==-1){
            DottUtils.ymlConfig.getConfig().set(path, def);
            DottUtils.ymlConfig.saveConfig();
            U.mensajeConsola("&cNo se ha detectado el path &f"+path+"&c en config. Regenerando con "+def+"...");
            return def;
        }
        return toGive;
    }
    public static long getLong(String path){
        return DottUtils.ymlConfig.getConfig().getLong(path, -1);
    }
    public static long getLong(String path, long def){
        long toGive = DottUtils.ymlConfig.getConfig().getLong(path, -1);
        if (toGive==-1){
            DottUtils.ymlConfig.getConfig().set(path, def);
            DottUtils.ymlConfig.saveConfig();
            U.mensajeConsola("&cNo se ha detectado el path &f"+path+"&c en config. Regenerando con "+def+"...");
            return def;
        }
        return toGive;
    }
    @Nullable
    public static String getString(@NotNull String path){
        return DottUtils.ymlConfig.getConfig().getString(path,  null);
    }
    @UnknownNullability
    public static String getString(@NotNull String path, String def){
        String toGive = DottUtils.ymlConfig.getConfig().getString(path, null);
        if (toGive==null){
            DottUtils.ymlConfig.getConfig().set(path, def);
            DottUtils.ymlConfig.saveConfig();
            U.mensajeConsola("&cNo se ha detectado el path &f"+path+"&c en config. Regenerando con "+def+"...");
            return def;
        }
        return toGive;
    }
    public static boolean getBoolean(@NotNull String path){
        return DottUtils.ymlConfig.getConfig().getBoolean(path,  false);
    }
    public static boolean getBoolean(@NotNull String path, boolean def){
        Object got = DottUtils.ymlConfig.getConfig().get(path);
        boolean success = (got instanceof Boolean);

        if (!success){
            DottUtils.ymlConfig.getConfig().set(path, def);
            DottUtils.ymlConfig.saveConfig();
            U.mensajeConsola("&cNo se ha detectado el path &f"+path+"&c en config. Regenerando con "+def+"...");
            return def;
        }
        return (Boolean) got;
    }
    //------ADMINS--------
    public static boolean addAdmin(String newAdmin){ // boolean = Was successful?
        if (admins.contains(newAdmin)) return false;

        admins.add(newAdmin);
        configLists.getConfig().set("adminlist", admins);
        configLists.saveConfig();
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
        configLists.getConfig().set("adminlist", admins);
        configLists.saveConfig();
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
    public static int getAdminsOnline() {
        int contador = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (containsAdmin(p.getName())) contador++;
        }

        return contador;
    }

    //-------MAINTENANCE-------
    /**
     * @return If the method took action
     */
    public static boolean onMaintenance(){ // boolean = Was successful?
        if (getMaintenanceStatus()) return false;

        config.getConfig().set("maintenance_active", true);
        config.saveConfig();
        maintenanceStatus = true;
        return true;
    }

    /**
     * @return If the method took action
     */
    public static boolean offMaintenance(){
        if (!getMaintenanceStatus()) return false;

        config.getConfig().set("maintenance_active", false);
        config.saveConfig();
        maintenanceStatus = false;
        return true;
    }
    public static boolean getMaintenanceStatus(){
        return maintenanceStatus;
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
    /**
     * @return true if the operation took effect!
     */
    public static boolean addWhitelist(@NotNull String newBlanco){ // boolean = Was successful?
        if (whitelist.contains(newBlanco)) return false;

        whitelist.add(newBlanco);
        configLists.getConfig().set("whitelist", whitelist);
        configLists.saveConfig();
        return true;
    }
    /**
     * @return true if the operation took effect!
     */
    public static boolean removeWhitelist(@NotNull String oldBlanco){ // boolean = Was successful?
        if (!whitelist.contains(oldBlanco)) return false;

        for (String blanco : new ArrayList<>(whitelist)) { // borrar repetidos en caso de
            if (blanco.equalsIgnoreCase(oldBlanco)) {
                whitelist.remove(blanco);
            }
        }

        //admins.remove(oldWhitelisted);
        configLists.getConfig().set("whitelist", whitelist);
        configLists.saveConfig();
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
    // -----------pvp------------------
    public static boolean getPvPStatus(){
        return pvpStatus;
    }
    /**
     * @return true if the operation took effect!
     */
    public static boolean onPvP(){ // boolean = Was successful?
        if (getPvPStatus()) return false;

        config.getConfig().set("pvp", true);
        config.saveConfig();
        pvpStatus = true;
        return true;
    }
    /**
     * @return True if the operation took effect!
     */
    public static boolean offPvP(){ // boolean = Was successful?
        if (!getPvPStatus()) return false;

        config.getConfig().set("pvp", false);
        config.saveConfig();
        pvpStatus = false;
        return true;
    }
    //---------------nofall-----------------
    public static boolean getNoFallStatus(){
        return noFallStatus;
    }
    /**
     * @return true if the operation took effect!
     */
    public static boolean onNoFall(){ // boolean = Was successful?
        if (getNoFallStatus()) return false;

        config.getConfig().set("no_fall", true);
        config.saveConfig();
        noFallStatus = true;
        return true;
    }

    /**
     * @return true if the operation took effect!
     */
    public static boolean offNoFall(){ // boolean = Was successful?
        if (!getNoFallStatus()) return false;

        config.getConfig().set("no_fall", false);
        config.saveConfig();
        noFallStatus = false;
        return true;
    }
}
