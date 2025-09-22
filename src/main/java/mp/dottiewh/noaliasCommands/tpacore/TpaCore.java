package mp.dottiewh.noaliasCommands.tpacore;

import mp.dottiewh.DottUtils;
import mp.dottiewh.config.Config;
import mp.dottiewh.noaliasCommands.backcore.BackCommand;
import mp.dottiewh.noaliasCommands.backcore.BackUtils;
import mp.dottiewh.utils.U;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TpaCore {
    private static final Map<String, BukkitRunnable> hashMap = new HashMap<>();
    private static final Map<String, BukkitRunnable> hashMapOfRemoving = new HashMap<>();
    private static final Map<String, BukkitRunnable> hashMapOfFinalStep = new HashMap<>();


    public static void addTpRequest(String nameFrom, String nameTo, Plugin plugin){
        Player from = Bukkit.getPlayerExact(nameFrom);
        Player to = Bukkit.getPlayerExact(nameTo);
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
        senderMsgPr("&ePuedes usar &6/tpacancel &epara cancelar todas tus solicitudes.", from);
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
        Player player = Bukkit.getPlayerExact(whoIsCancelling);
        if (player==null){
            U.mensajeConsolaNP("&cJugador no conectado. (TpaCore.java 1) "+whoIsCancelling);
            return;
        }
        if (hashMapOfFinalStep.containsKey(whoIsCancelling)){
            if(checkAndCancelTask(player.getName())){
                senderMsgPr("&6Has cancelado tus tpa a último momento.", player);
                return;
            }
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

                    Player pTo = Bukkit.getPlayerExact(to);
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
        Player player = Bukkit.getPlayerExact(whoIsAccepting);
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
                    if(checkFinalTask(from)){
                        senderMsgPr("&eSaltando a &f"+from+"&e... (Ya aceptado)", player);
                        continue;
                    }
                    lastStep(from, bothNames);
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
        Player player = Bukkit.getPlayerExact(whoIsAccepting);
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
                    Player pFrom = Bukkit.getPlayerExact(from);
                    if(checkAndCancelTask(from)){
                        senderMsgPr("&eLe has rechazado el tpa a &f"+from+" &ePor los pelos!", player);
                        continue;
                    }
                    hashMap.remove(bothNames);
                    hashMapOfRemoving.get(bothNames).cancel();
                    hashMapOfRemoving.remove(bothNames);
                    senderMsgPr("&eLe has rechazado el tpa a &f"+from+" &ecorrectamente!", player);

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
    private static void lastStep(String firstName, String bothNames){
        Player pFrom = Bukkit.getPlayerExact(firstName);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                hashMap.get(bothNames).run();
                hashMap.remove(bothNames);
                hashMapOfFinalStep.get(firstName).cancel();
                hashMapOfFinalStep.remove(firstName);
            }
        };
        runnable.runTaskLater(DottUtils.getPlugin(), 100L);
        senderMsgPr("&eNo te muevas. Te tepearas en 5 (s)...", pFrom);
        hashMapOfFinalStep.put(firstName, runnable);
    }

    //
    public static void movementManagement(PlayerMoveEvent event, Player player){
        if (event.isCancelled()) return;

        double x_from, z_from, x_to, z_to;
        x_from = event.getFrom().getBlockX();
        z_from = event.getFrom().getBlockZ();

        x_to = event.getTo().getBlockX();
        z_to = event.getTo().getBlockZ();

        if (x_from!=x_to||z_from!=z_to){
            if (checkAndCancelTask(player.getName())){
                senderMsgPr("&cTe has movido, así que se ha cancelado tu &6tpa&c!", player);
            }
        }
    }
    private static boolean checkAndCancelTask(String name){
        if(!hashMapOfFinalStep.containsKey(name)) return false;

        hashMapOfFinalStep.get(name).cancel();
        hashMapOfFinalStep.remove(name);
        for (String bothNames : hashMap.keySet()){
            String from = bothNames.split(";")[0];
            if (from.equals(name)) {
                hashMap.remove(bothNames);
            }
        }
        return true;
    }
    private static boolean checkFinalTask(String name){
        return hashMapOfFinalStep.containsKey(name);
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

        if (args.length<1){
            senderMsgPr("&cPor favor añade un nombre.", sender);
            return true;
        }
        String input = args[0];
        Player target = Bukkit.getPlayerExact(input);
        if (target==null){
            senderMsgPr("&cEl jugador &f"+input+" &cno está conectado actualmente.", sender);
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
