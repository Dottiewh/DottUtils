package mp.dottiewh.noaliasCommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.Commands;
import mp.dottiewh.utils.U;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class Coordenadas extends Commands {
    private static final Map<UUID, BukkitTask> taskMap = new HashMap<>();

    public Coordenadas(CommandContext<CommandSourceStack> ctx) {
        super(ctx);
        run();
    }

    @Override
    protected void run(){
        if (!(sender instanceof Player player)){
            senderMessageNP("&cEste comando solo lo puede usar un jugador.");
            return;
        }
        //---
        UUID uuid = player.getUniqueId();

        if (taskMap.containsKey(uuid)){
            cancelAndRemove(uuid, taskMap.get(uuid));
            senderMessageNP("&8> &eCoords display &cOff!");
        }else{
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    int x = player.getLocation().getBlockX();
                    int y = player.getLocation().getBlockY();
                    int z = player.getLocation().getBlockZ();
                    BlockFace blockFace = player.getFacing();
                    String facing = String.valueOf(blockFace).toLowerCase();
                    String toDisplay = String.join("&6, &c&l", String.valueOf(x), String.valueOf(y), String.valueOf(z));

                    player.sendActionBar(U.mensajeConColor("&c&l"+toDisplay+" &8| &6(&a"+facing+"&6)"));
                }
            }.runTaskTimer(plugin, 0L, 10L);

            taskMap.put(uuid, task);
            senderMessageNP("&8> &eCoords display &aOn!");
        }
    }
    private void cancelAndRemove(UUID uuid, BukkitTask task){
        task.cancel();
        taskMap.remove(uuid);
    }

    //
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(String cmd){
        return literal(cmd)
                .executes(ctx->{
                    new Coordenadas(ctx);
                    return 1;
                })
                //
                ;
    }
}
