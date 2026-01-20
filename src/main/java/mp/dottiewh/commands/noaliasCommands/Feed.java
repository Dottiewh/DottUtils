package mp.dottiewh.commands.noaliasCommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import mp.dottiewh.commands.ReferibleCommand;
import mp.dottiewh.utils.U;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class Feed extends ReferibleCommand {

    public Feed(CommandContext<CommandSourceStack> ctx, CommandSender sender) {
        super(ctx, sender);
        if(isListEmpty) return;
        run();
    }

    public Feed(CommandContext<CommandSourceStack> ctx, List<Player> playerList) {
        super(ctx, playerList);
        if(isListEmpty) return;
        run();
    }

    @Override
    protected void run() {
        for(Player p : playerList){
            coreHeal(p);
        }
        senderMessageNP("&8&l> &aLe has regenerado la comida al máximo a &f"+getOutput("&f")+"&a.");
    }
    private void coreHeal(Player player){
        player.setFoodLevel(20);
        player.setSaturation(20f);
        U.targetMessageNP(player, "&8&l> &eTe han regenerado la comida al máximo.");
    }

    //
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("feed")
                .requires(ctx -> ctx.getSender().hasPermission("DottUtils.feed"))
                .then(io.papermc.paper.command.brigadier.Commands.argument("players", ArgumentTypes.players())
                        .executes(ctx->{
                            new Feed(ctx, getPlayerListFromCtx(ctx));
                            return 1;
                        })
                )
                .executes(ctx->{
                    new Feed(ctx, ctx.getSource().getSender());
                    return 1;
                })
                //
                ;
    }
}
