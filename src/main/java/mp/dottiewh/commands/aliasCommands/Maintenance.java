package mp.dottiewh.commands.aliasCommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.commands.Commands;
import mp.dottiewh.config.Config;
import mp.dottiewh.utils.U;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class Maintenance extends Commands {
    private final String type;

    public Maintenance(CommandContext<CommandSourceStack> ctx, String type) {
        super(ctx);
        this.type=type;

        run();
    }

    @Override
    protected void run(){
        switch (type){
            case "toggle" -> toggle();
            case "status" -> status();

            default -> {}
        }

    }

    private void status(){
        String format = (Config.getMaintenanceStatus()) ? "&aACTIVADO" : "&cDESACTIVADO";
        senderMessage("&9El mantenimiento está: "+format);
    }
    private void toggle(){
        boolean status = Config.getMaintenanceStatus();

        if (status) { // si es true, desactiva la whitelist
            Config.offMaintenance();
            senderMessage("&9&lMantenimiento &cDESACTIVADO&9&l.");
        }
        else {// si es false la activa
            Config.onMaintenance();
            senderMessage("&9&lMantenimiento &aACTIVADO&9&l.");
        }
    }
    //-------------------------

    public static void checkMaintenance(AsyncPlayerPreLoginEvent event){
        String name = event.getName();

        if (!Config.getMaintenanceStatus()) return; // return if off.
        if (Config.containsAdmin(name)) return;

        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, U.mensajeConColor("&8<<< &6El server está en mantenimiento! &8>>>"));
    }

    //----------------------------------
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("maintenance")
                .then(literal("toggle")
                        .executes(ctx -> {
                            new Maintenance(ctx, "toggle");
                            return 1;
                        })
                )
                .then(literal("status")
                        .executes(ctx -> {
                            new Maintenance(ctx, "status");
                            return 1;
                        })
                );
    }
}
