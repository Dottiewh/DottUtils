package mp.dottiewh.commands.aliasCommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import mp.dottiewh.commands.ReferibleCommand;
import mp.dottiewh.utils.U;
import org.bukkit.entity.Player;

import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class TellRaw extends ReferibleCommand {
    String text;

    public TellRaw(CommandContext<CommandSourceStack> ctx, String text, List<Player> pList) {
        super(ctx, pList);
        if (isListEmpty) return;

        this.text = text;
        run();
    }

    @Override
    protected void run() {
        for (Player p : playerList) {
            U.targetMessageNP(p, text);
        }
        senderMessage("&aLe has mandado un tellraw a &f" + getOutput("&f"));
    }

    //----------------------------------
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("tellraw")
                .then(io.papermc.paper.command.brigadier.Commands.argument("players", ArgumentTypes.players())
                        .then(io.papermc.paper.command.brigadier.Commands.argument("text", StringArgumentType.string())
                                .executes(ctx->{
                                    String text = ctx.getArgument("text", String.class);
                                    new TellRaw(ctx, text, getPlayerListFromCtx(ctx));
                                    return 1;
                                })
                        )
                );
    }
}
