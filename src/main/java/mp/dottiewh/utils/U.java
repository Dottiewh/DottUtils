package mp.dottiewh.utils;

import mp.dottiewh.DottUtils;
import mp.dottiewh.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
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
        target.sendMessage(U.mensajeConPrefix(mensaje));
    }
    public static void targetMessageNP(Player target, String mensaje){
        target.sendMessage(U.mensajeConColor(mensaje));
    }
    public static Component mensajeConPrefix(String mensaje){
        return componentColor(prefix+mensaje);
    }
    public static Component mensajeConColor(String mensaje){
        return componentColor(mensaje);
    }
    public static void mensajeConsola(String mensaje){
        Bukkit.getConsoleSender().sendMessage(componentColor(prefix+mensaje));
    }
    public static void mensajeConsolaNP(String mensaje){
        Bukkit.getConsoleSender().sendMessage(componentColor(mensaje));
    }

    public static String componentToStringMsg(Component component){
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }
    public static Component componentColor(String mensaje){
        return LegacyComponentSerializer.legacy('&').deserialize(mensaje);
    }
    public static void showAllStatus(){
        boolean noFallS = Config.getNoFallStatus(), wlS = Config.getWhiteListStatus();
        boolean pvpS = Config.getPvPStatus();

        String displayNoFallS = (noFallS) ? "&eHay daño de caída &cDESACTIVADO&e." : "&eHay daño de caída &aACTIVADO&e.";
        String displayWlS = (wlS) ? "&eLa whitelist está &aACTIVADA&e." : "&eLa whitelist está &cDESACTIVADA&e.";
        String displaypvpS = (pvpS) ? "&eEl pvp está &aACTIVADO&e." : "&eEl pvp está &cDESACTIVADO&e.";

        mensajeConsolaNP(displayNoFallS);
        mensajeConsolaNP(displayWlS);
        mensajeConsolaNP(displaypvpS);

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

        if(!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) return;

        event.setCancelled(true);
    }
    public static String getMsgPath (String path){
        return DottUtils.ymlMessages.getConfig().getString(path);
    }
    public static int getIntConfigPath (String path){
        return DottUtils.ymlConfig.getConfig().getInt(path);
    }
    public static double truncar(double value, int decimales){
        double factor = Math.pow(10, decimales);
        return Math.floor(value*factor) / factor;
    }
}
