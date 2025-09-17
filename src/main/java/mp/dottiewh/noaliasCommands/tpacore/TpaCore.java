package mp.dottiewh.noaliasCommands.tpacore;

import mp.dottiewh.config.Config;
import mp.dottiewh.utils.U;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class TpaCore {
    private static final Map<String, BukkitTask> hashMap = new HashMap<>();

    //----
    public static void senderMsgPr(String msg, CommandSender sender){
        String prefix = U.getMsgPath("tpa_prefix", "&6&l[&e&lTpa&6&l] &f");

        sender.sendMessage(U.mensajeConColor(prefix+msg));
    }
    public static boolean failedGlobalTpaChecks(CommandSender sender){
        return mainCheck(sender);
    }
    public static boolean failedGlobalTpaChecks(CommandSender sender, String[] args){
        if(mainCheck(sender)) return true;

        if (args.length>1){
            senderMsgPr("Por favor añade un nombre.", sender);
            return true;
        }
        return false;
    }

    private static boolean mainCheck(CommandSender sender){
        if (!Config.getBoolean("tpa_active")){
            senderMsgPr("El tpa está desactivado en la config.", sender);
            return true;
        }
        if (!(sender instanceof Player)){
            senderMsgPr("&cEste comando solo lo puede usar un jugador.", sender);
            return true;
        }

        return false;
    }
}
