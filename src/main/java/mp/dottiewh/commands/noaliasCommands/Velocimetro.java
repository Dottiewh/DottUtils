package mp.dottiewh.commands.noaliasCommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.commands.miscs.ToggletableTargetCommand;
import mp.dottiewh.utils.U;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class Velocimetro extends ToggletableTargetCommand {
    private static final Map<UUID, BukkitTask> taskMap = new HashMap<>();

    private static final String className = "Velocimetro";


    public Velocimetro(CommandContext<CommandSourceStack> ctx) {
        super(ctx, className);
        //run();
    }

    @Override
    public void onEnable() {
        SpeedController speedController = new SpeedController(player);

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                double speed = speedController.getBlocksPerSecond();
                speedController.addLoc();
                player.sendActionBar(U.mensajeConColor("&6Estás yendo a &f"+speed+" &7(b/s)&6."));
            }
        }.runTaskTimer(plugin, 0L, 20L);

        taskMap.put(playerUUID, task);
        senderMessageNP("&8> &eVelocímetro &e&ohorizontal &r&aActivado!");
    }
    @Override
    public void onDisable() {
        cancelAndRemove(playerUUID, taskMap.get(playerUUID));
        senderMessageNP("&8> &eVelocímetro &e&ohorizontal &r&cDesactivado!");
    }

    private void cancelAndRemove(UUID uuid, BukkitTask task){
        task.cancel();
        taskMap.remove(uuid);
        SpeedController.clearPlayer(uuid);
    }
    //==
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(String cmd){
        return literal(cmd)
                .executes(ctx->{
                    new Velocimetro(ctx);
                    return 1;
                })
                //
                ;
    }

    //===================
    private static class SpeedController {
        private final Player player;
        private final UUID uuid;

        public SpeedController(Player player) {
            this.player = player;
            this.uuid = player.getUniqueId();
        }

        private static final HashMap<UUID, Location> previousLocMap = new HashMap<>();

        public void addLoc(){
            previousLocMap.put(uuid, player.getLocation());
        }
        public Location getLastLocationSaved(){
            return previousLocMap.getOrDefault(uuid, player.getLocation());
        }
        public void clearPlayer(){
            clearPlayer(uuid);
        }
        public static void clearPlayer(UUID uuid){
            previousLocMap.remove(uuid);
        }

        public double getBlocksPerSecond(){
            return getBlocksPerSecond(2);
        }
        public double getBlocksPerSecond(int decimales){
            Vector movement = player.getLocation().toVector()
                    .subtract(getLastLocationSaved().toVector())
                    .setY(0);
            return U.truncar(movement.length(), decimales);
        }
    }
}
