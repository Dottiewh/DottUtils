package mp.dottiewh;

import mp.dottiewh.config.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.*;

import static mp.dottiewh.DottUtils.prefix;

public class U { //Stands for utils
    private static Set<UUID> listaNoFall = new HashSet<>();
    private static CustomConfig config;
    private static List<String> admins;

    //--------------------------Métodos Útiles-----------------------------------
    public static String mensajeConPrefix(String mensaje){
        return ChatColor.translateAlternateColorCodes('&',prefix+"&f"+mensaje);
    }
    public static void mensajeConsola(String mensaje){
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',prefix+mensaje));
    }

    //---------------no fall management---------
    public static void noFall_add(Player player){
        UUID uuid = player.getUniqueId();
        listaNoFall.add(uuid);
    }
    public static void noFall_remove(Player player){
        UUID uuid = player.getUniqueId();
        listaNoFall.remove(uuid);
    }
    public static boolean noFall_check(Player player){
        UUID uuid = player.getUniqueId();
        return listaNoFall.contains(uuid);
    }
    public static void noFall_core(EntityDamageEvent event){
        if (!(event.getEntity() instanceof Player player)) return;
        if (noFall_check(player)){
            event.setCancelled(true);
            noFall_remove(player);
        }
    }

    //------config--------
    public static void configInit(){
        config = DottUtils.getRegisteredConfig();
        admins = config.getConfig().getStringList("adminlist");
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
        return admins.contains(name);
    }
    public static List<String> getAdminList(){
        return admins;
    }
}
