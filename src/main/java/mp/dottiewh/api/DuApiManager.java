package mp.dottiewh.api;

import mp.dottiewh.config.Config;
import mp.dottiewh.utils.U;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class DuApiManager {
    Plugin plugin;
    String prefix;

    public DuApiManager(Plugin plugin, String prefix) {
        this.plugin = plugin;
        this.prefix = prefix;
    }
    //---

    public List<String> getAdminList(){return Config.getAdminList();}
    public boolean containsAdmin(String adm){return Config.containsAdmin(adm);}

    public boolean enablePvP(){return Config.onPvP();}
    public boolean disablePvP(){return Config.offPvP();}
    //
    public void targetMsg(Player player, String msg){
        targetMsgNP(player, prefix+msg);
    }
    public void targetMsgNP(Player p, String msg){
        U.targetMessageNP(p, msg);
    }
    public Component mensajeConPrefix(String msg){
        return mensajeConColor(prefix+msg);
    }
    public Component mensajeConColor(String msg){
        return U.mensajeConColor(msg);
    }
    public void mensajeConsola(String msg){
        mensajeConsolaNP(prefix+msg);
    }
    public void mensajeConsolaNP(String msg){
        U.mensajeConsolaNP(msg);
    }
    public String componentToString(Component component){
        return U.componentToStringMsg(component);
    }
    public Component componentFromString(String msg){
        return U.componentColor(msg);
    }
}
