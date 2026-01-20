package mp.dottiewh.commands.noaliasCommands.tpacore;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.commands.Commands;
import org.bukkit.entity.Player;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class TpaDeny extends Commands {
    public TpaDeny(CommandContext<CommandSourceStack> ctx) {
        super(ctx);
        if(TpaCore.failedGlobalTpaChecks(sender)) return;
        run();

    }

    @Override
    protected void run() {
        Player player = (Player) sender;
        TpaCore.tpadeny(player.getName());
    }

    //
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("tpadeny")
                .executes(ctx->{
                    new TpaDeny(ctx);
                    return 1;
                })
                //
                ;
    }
}
