package mp.dottiewh;

import mp.dottiewh.config.CustomConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.*;

import static mp.dottiewh.DottUtils.prefix;

public class U { //Stands for utils
    private static Set<UUID> listaNoFall = new HashSet<>();


    //--------------------------Métodos Útiles-----------------------------------
    public static void targetMessage(Player target, String mensaje){
        target.sendMessage(U.mensajeConPrefix(mensaje));
    }
    public static void targetMessageNP(Player target, String mensaje){
        target.sendMessage(U.mensajeConColor(mensaje));
    }
    public static String mensajeConPrefix(String mensaje){
        return ChatColor.translateAlternateColorCodes('&',prefix+"&f"+mensaje);
    }
    public static String mensajeConColor(String mensaje){
        return ChatColor.translateAlternateColorCodes('&',mensaje);
    }
    public static void mensajeConsola(String mensaje){
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',prefix+mensaje));
    }
    public static void mensajeConsolaNP(String mensaje){
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',mensaje));
    }
    public static String componentGetContent(Component component){
        String msg = PlainTextComponentSerializer.plainText().serialize(component);

        return msg;
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


}
