package mp.dottiewh.commands.noaliasCommands.backcore;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.commands.Commands;
import mp.dottiewh.DottUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class BackCommand extends Commands {
    private static final Map<UUID, BukkitTask> onGoingTasksMap = new HashMap<>();

    public BackCommand(CommandContext<CommandSourceStack> ctx) {
        super(ctx);
        run();
    }

    @Override
    protected void run(){
        if (!DottUtils.ymlConfig.getConfig().getBoolean("back_active")){ //Si está en true, sigue, si no no
            BackUtils.backSendMsgSender("&cEl back está desactivado en la config.", sender);
            return;
        }


        long delay = DottUtils.ymlConfig.getConfig().getLong("delay_onback");

        if (!(sender instanceof Player player)){
            BackUtils.backSendMsgSender("&cEste comando solo lo puede usar un jugador.", sender);
            return;
        }
        String name = player.getName();
        UUID uuid = player.getUniqueId();
        Location loc = BackUtils.getDeathLoc(name, uuid);
        if (loc==null){
            BackUtils.backSendMsgSender("&cNo tienes ningun &6back &casociado.", sender);
            return;
        }

        //-------MANEJO DE TASKS------------
        if (checkAndCancelTask(player)){
            senderMessageNP("&cCancelando...");
        }

        BackUtils.backSendMsgSender("&aTe vas a teletransportar en &e"+(delay/20)+" (s)&a.", sender);
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

    //
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("back")
                .executes(ctx->{
                    new BackCommand(ctx);
                    return 1;
                })
                //
                ;
    }
}
