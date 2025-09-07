package mp.dottiewh.noaliasCommands.backcore;

import mp.dottiewh.Commands;
import mp.dottiewh.DottUtils;
import mp.dottiewh.Utils.U;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BackCommand extends Commands {
    private static final Map<UUID, BukkitTask> onGoingTasksMap = new HashMap<>();

    public BackCommand(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);
        run();
    }
    @Override
    protected void run(){
        long delay = DottUtils.ymlConfig.getConfig().getLong("delay_onback");

        if (!(sender instanceof Player player)){
            senderMessage("&cEste comando solo lo puede usar un jugador.");
            return;
        }
        String name = player.getName();
        Location loc = BackUtils.getDeathLoc(name);
        if (loc==null){
            senderMessage("&cNo tienes ningun &6back &casociado.");
            return;
        }
        UUID uuid = player.getUniqueId();

        //-------MANEJO DE TASKS------------
        if (checkAndCancelTask(player)){
            senderMessageNP("&cCancelando...");
        }

        senderMessage("&aTe vas a teletransportar en &e"+(delay/20)+" (s)&a.");
        //manejo de los 5 segundos de tp
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(loc);
                onGoingTasksMap.remove(uuid);
                BackUtils.delDeathLoc(name);
            }
        }.runTaskLater(plugin, delay); // delay = espera
        onGoingTasksMap.put(uuid, task);
    }

    public static Map<UUID, BukkitTask> getMap(){
        return onGoingTasksMap;
    }
    public static boolean checkAndCancelTask(Player player){
        UUID uuid = player.getUniqueId();

        if (onGoingTasksMap.containsKey(uuid)){
            onGoingTasksMap.get(uuid).cancel();
            onGoingTasksMap.remove(uuid);
            return true;
        }
        return false;
    }
}
