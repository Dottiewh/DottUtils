package mp.dottiewh.commands.noaliasCommands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import mp.dottiewh.commands.ReferibleCommand;
import mp.dottiewh.utils.U;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class Jump extends ReferibleCommand {
    private static final Map<UUID, BukkitTask> delayMap = new HashMap<>();
    double power;
    int times;
     // in ticks


    public Jump(CommandContext<CommandSourceStack> ctx, List<Player> playerList, double power, int times) {
        super(ctx, playerList);
        if(isListEmpty) return;
        this.power=power;
        this.times=times;
        run();
    }

    @Override
    protected void run() {

        long delayNoFall = 600L;
        //String errorMsg = "&c&lNo has introducido un valor valido.\n&6Posibles valores: &eNúmero | &e&oNombre de un jugador &8| &e&oNúmero";


        if (times > 50) {
            senderMessage("&cControlate! &e(max 50)&c.");
            return;
        }

        if(power>30) power=30;

        //core!
        for (Player target : playerList){
            if (delayMap.containsKey(target.getUniqueId())) delayMap.get(target.getUniqueId()).cancel(); // se cancela si hay una tarea
            //------end of checks
            for (int i = 0; i < times; i++) {
                int delay = 5 * i;

                final double finalInput = power;
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    target.setVelocity(new Vector(0, finalInput, 0));
                }, delay);

            }
            // anti caída
            U.noFall_add(target);

            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    delayMap.remove(target.getUniqueId());
                    U.noFall_remove(target);
                }
            }.runTaskLater(plugin, delayNoFall); // delay = espera
            // Guardamos la nueva tarea
            delayMap.put(target.getUniqueId(), task);
        }
        senderMessageNP("&aHas hecho saltar a &f"+getOutput("&f")+"&a. &e("+power+"&e) &8&l| &e("+times+"&e)");
    }

    //
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("jump")
                .requires(ctx -> ctx.getSender().hasPermission("DottUtils.jump"))
                .then(io.papermc.paper.command.brigadier.Commands.argument("players", ArgumentTypes.players())
                        .then(io.papermc.paper.command.brigadier.Commands.argument("power", DoubleArgumentType.doubleArg(0, 30))
                                .executes(ctx->{
                                    double power = ctx.getArgument("power", Double.class);
                                    new Jump(ctx, getPlayerListFromCtx(ctx), power,1 );
                                    return 1;
                                })
                                .then(io.papermc.paper.command.brigadier.Commands.argument("times", IntegerArgumentType.integer(0))
                                        .executes(ctx->{
                                            double power = ctx.getArgument("power", Double.class);
                                            int times = ctx.getArgument("times", Integer.class);
                                            new Jump(ctx, getPlayerListFromCtx(ctx), power, times);
                                            return 1;
                                        })
                                )
                        )
                )
                //
                ;
    }
}
