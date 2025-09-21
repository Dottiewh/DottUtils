package mp.dottiewh.noaliasCommands.tpacore;

import mp.dottiewh.config.Config;
import mp.dottiewh.noaliasCommands.backcore.BackUtils;
import mp.dottiewh.utils.U;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class TpaCore {
    private static final Map<String, BukkitRunnable> hashMap = new HashMap<>();
    private static final Map<String, BukkitRunnable> hashMapOfRemoving = new HashMap<>();


    public static void addTpRequest(String nameFrom, String nameTo, Plugin plugin){
        Player from = Bukkit.getPlayer(nameFrom);
        Player to = Bukkit.getPlayer(nameTo);
        if (from==null||to==null) throw new IllegalArgumentException("Alguno de los dos inputs es null.");

        String bothNames = String.join(";", nameFrom, nameTo);
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if(Config.getBoolean("save_back_when_tpa")){
                    BackUtils.addDeathLoc(from);
                }
                from.teleport(to.getLocation());
                senderMsgPr("&aTe has teletransportado a &f"+nameTo+" &acorrectamente!", from);
            }
        };
        senderMsgPr("&aLe has enviado una solicitud de tpa a &f"+nameTo+" &acorrectamente!", from);
        senderMsgPr("&ePuedes usar /tpacancel para cancelar todas tus solicitudes.", from);
        senderMsgPr("&eEl jugador &f"+nameFrom+" &ete ha enviado una solicitud de Tpa.", to);
        senderMsgPr("&eUsa: &6/tpaaccept, /tpadeny", to);
        putAndCooldown(from, bothNames, task, plugin);

    }
    private static void putAndCooldown(Player playerFrom, String bothNames, BukkitRunnable task, Plugin plugin){
        long cooldown = Config.getLong("petition_active_for", 60)*20;
        hashMap.put(bothNames, task);

        String to = bothNames.split(";")[1];
        BukkitRunnable removing = new BukkitRunnable() {
            @Override
            public void run() {
                hashMapOfRemoving.remove(bothNames);
                senderMsgPr("&cTu petición hacia &f"+to+" &cha expirado.", playerFrom);
            }
        };
        removing.runTaskLater(plugin, cooldown);
        hashMapOfRemoving.put(bothNames, removing);
    }
    //cancel
    public static void tpacancel(String whoIsCancelling){
        Player player = Bukkit.getPlayer(whoIsCancelling);
        if (player==null){
            U.mensajeConsolaNP("&cJugador no conectado. (TpaCore.java 1) "+whoIsCancelling);
            return;
        }
        // chekis
        boolean success = false;
        for (String bothNames : hashMap.keySet()){
            String from = bothNames.split(";")[0];
            if (from.equals(whoIsCancelling)) {
                success = true;
                break;
            }
        }
        //
        if(success){
            for(Map.Entry<String, BukkitRunnable> mapa : hashMap.entrySet()){
                String bothNames = mapa.getKey();
                String from = bothNames.split(";")[0];
                String to = bothNames.split(";")[1];
                if(to.equals(whoIsCancelling)){
                    hashMap.remove(bothNames);
                    hashMapOfRemoving.get(bothNames).cancel();
                    hashMapOfRemoving.remove(bothNames);
                    senderMsgPr("&eLe has cancelado el tpa a &f"+to+" &ecorrectamente!", player);

                    Player pTo = Bukkit.getPlayer(to);
                    if(pTo!=null){
                        senderMsgPr("&cEl jugador &4"+from+" &cte ha cancelado la solicitud.", pTo);
                    }
                }
            }
        }else{
            senderMsgPr("&cNo tienes ninguna solicitud de tpa.", player);
        }
    }
    //accept and deny
    public static void tpaccept(String whoIsAccepting){
        Player player = Bukkit.getPlayer(whoIsAccepting);
        if (player==null){
            U.mensajeConsolaNP("&cJugador no conectado. (TpaCore.java 2) "+whoIsAccepting);
            return;
        }
        //
        if(checkForInHashMap(whoIsAccepting)){
            for(Map.Entry<String, BukkitRunnable> mapa : hashMap.entrySet()){
                String bothNames = mapa.getKey();
                String from = bothNames.split(";")[0];
                String to = bothNames.split(";")[1];
                if(to.equals(whoIsAccepting)){
                    hashMap.get(bothNames).run();
                    hashMap.remove(bothNames);
                    hashMapOfRemoving.get(bothNames).cancel();
                    hashMapOfRemoving.remove(bothNames);
                    senderMsgPr("&aLe has aceptado el tpa a &f"+from+" &acorrectamente!", player);
                }
            }
        }else{
            senderMsgPr("&cNo tienes ninguna solicitud de tpa.", player);
        }

    }
    public static void tpadeny(String whoIsAccepting){
        Player player = Bukkit.getPlayer(whoIsAccepting);
        if (player==null){
            U.mensajeConsolaNP("&cJugador no conectado. (TpaCore.java 3) "+whoIsAccepting);
            return;
        }
        //
        if(checkForInHashMap(whoIsAccepting)){
            for(Map.Entry<String, BukkitRunnable> mapa : hashMap.entrySet()){
                String bothNames = mapa.getKey();
                String from = bothNames.split(";")[0];
                String to = bothNames.split(";")[1];
                if(to.equals(whoIsAccepting)){
                    hashMap.remove(bothNames);
                    hashMapOfRemoving.get(bothNames).cancel();
                    hashMapOfRemoving.remove(bothNames);
                    senderMsgPr("&eLe has rechazado el tpa a &f"+from+" &ecorrectamente!", player);

                    Player pFrom = Bukkit.getPlayer(from);
                    if(pFrom!=null){
                        senderMsgPr("&cEl jugador &4"+to+" &cte ha rechazado tu solicitud.", pFrom);
                    }
                }
            }
        }else{
            senderMsgPr("&cNo tienes ninguna solicitud de tpa.", player);
        }
    }

    private static boolean checkForInHashMap(String whoIsAccepting){
        for (String bothNames : hashMap.keySet()){
            String to = bothNames.split(";")[1];
            if (to.equals(whoIsAccepting)) {
                return true;
            }
        }
        return false;
    }
    //----
    public static void senderMsgPr(String msg, CommandSender sender){
        String prefix = U.getMsgPath("tpa_prefix", "&6&l[&e&lTpa&6&l] &f");

        sender.sendMessage(U.mensajeConColor(prefix+msg));
    }
    public static void senderMsgPr(String msg, Player player){
        if (player==null) return;
        String prefix = U.getMsgPath("tpa_prefix", "&6&l[&e&lTpa&6&l] &f");
        player.sendMessage(U.mensajeConColor(prefix+msg));
    }
    public static boolean failedGlobalTpaChecks(CommandSender sender){
        return mainCheck(sender);
    }
    // only for /tpa
    public static boolean failedGlobalTpaChecks(CommandSender sender, String[] args){
        if(mainCheck(sender)) return true;

        if (args.length>1){
            senderMsgPr("Por favor añade un nombre.", sender);
            return true;
        }
        String input = args[0];
        Player target = Bukkit.getPlayer(input);
        if (target==null){
            senderMsgPr("&cEl jugador "+input+" no está conectado actualmente.", sender);
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
