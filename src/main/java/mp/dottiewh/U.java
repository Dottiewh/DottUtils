package mp.dottiewh;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import static mp.dottiewh.DottUtils.prefix;

public class U { //Stands for utils

    //--------------------------Métodos Útiles-----------------------------------
    public static String mensajeConPrefix(String mensaje){
        return ChatColor.translateAlternateColorCodes('&',prefix+"&f"+mensaje);
    }
    public static void mensajeConsola(String mensaje){
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',prefix+mensaje));
    }
}
