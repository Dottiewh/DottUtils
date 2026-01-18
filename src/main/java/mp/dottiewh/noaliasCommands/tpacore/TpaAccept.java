package mp.dottiewh.noaliasCommands.tpacore;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.Commands;

import mp.dottiewh.noaliasCommands.Coordenadas;
import org.bukkit.entity.Player;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class TpaAccept extends Commands {

    public TpaAccept(CommandContext<CommandSourceStack> ctx) {
        super(ctx);
        if(TpaCore.failedGlobalTpaChecks(sender)) return;
        run();
    }

    @Override
    protected void run() {
        Player player = (Player) sender;
        TpaCore.tpaccept(player.getName());
    }

    //
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("tpaaccept")
                .executes(ctx->{
                    new TpaAccept(ctx);
                    return 1;
                })
                //
                ;
    }
}
