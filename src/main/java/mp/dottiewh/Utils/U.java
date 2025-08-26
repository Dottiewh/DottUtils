package mp.dottiewh.Utils;

import mp.dottiewh.DottUtils;
import mp.dottiewh.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.*;

import static mp.dottiewh.DottUtils.prefix;

public class U { //Stands for utils
    private static final Set<UUID> listaNoFall = new HashSet<>();
   // private static final MiniMessage miniMessage = MiniMessage.miniMessage();


    //--------------------------Métodos Útiles-----------------------------------
    public static void targetMessage(Player target, String mensaje){
        target.sendMessage(U.STmensajeConPrefix(mensaje));
    }
    public static void targetMessageNP(Player target, String mensaje){
        target.sendMessage(U.STmensajeConColor(mensaje));
    }
    public static String STmensajeConPrefix(String mensaje){
        return ChatColor.translateAlternateColorCodes('&',prefix+"&f"+mensaje);
    }
    public static String STmensajeConColor(String mensaje){
        return ChatColor.translateAlternateColorCodes('&',mensaje);
    }
    public static Component componentColor(String mensaje){
        return LegacyComponentSerializer.legacy('&').deserialize(mensaje);
    }
    public static void STmensajeConsola(String mensaje){
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',prefix+mensaje));
    }
    public static void STmensajeConsolaNP(String mensaje){
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',mensaje));
    }
    public static String componentToStringMsg(Component component){
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }
    public static void showAllStatus(){
        boolean noFallS = Config.getNoFallStatus(), wlS = Config.getWhiteListStatus();
        boolean pvpS = Config.getPvPStatus();


        STmensajeConsolaNP("&9Whitelist: &e"+wlS);
        STmensajeConsolaNP("&9PvP: &e"+pvpS);
        STmensajeConsolaNP("&9No Fall: &e"+noFallS);

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
    //-------------other-------------------
    public static void noPvP(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return; //victim
        if (!(event.getDamager() instanceof Player)) return; //damager

        if (Config.getPvPStatus()) return; // si pvp en true, se devuelve

        event.setCancelled(true);
    }
    public static void noFall(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!Config.getNoFallStatus()) return; //si esta en false el no fall se devuelve

        event.setCancelled(true);
    }
    public static String getMsgPath (String path){
        return DottUtils.ymlMessages.getConfig().getString(path);
    }
    public static int getIntConfigPath (String path){
        return DottUtils.ymlConfig.getConfig().getInt(path);
    }
}
