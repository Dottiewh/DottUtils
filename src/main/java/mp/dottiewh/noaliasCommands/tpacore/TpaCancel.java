package mp.dottiewh.noaliasCommands.tpacore;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.Commands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class TpaCancel extends Commands {
    public TpaCancel(CommandContext<CommandSourceStack> ctx) {
        super(ctx);

        if(TpaCore.failedGlobalTpaChecks(sender)) return;
        run();
    }

    @Override
    protected void run() {
        Player player = (Player) sender;
        TpaCore.tpacancel(player.getName());
    }

    //
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("tpacancel")
                .executes(ctx->{
                    new TpaCancel(ctx);
                    return 1;
                })
                //
                ;
    }
}
