package mp.dottiewh.commands.noaliasCommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import mp.dottiewh.commands.ReferibleCommand;
import mp.dottiewh.utils.U;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class Heal extends ReferibleCommand {

    public Heal(CommandContext<CommandSourceStack> ctx, List<Player> playerList) {
        super(ctx, playerList);
        if(isListEmpty) return;

        run();
    }

    public Heal(CommandContext<CommandSourceStack> ctx, CommandSender sender) {
        super(ctx, sender);
        if(isListEmpty) return;

        run();
    }

    @Override
    protected void run() {
        for(Player p : playerList){
            core(p);
        }

        senderMessageNP("&8&l> &aSe le ha regenerado la vida correctamente a &f"+getOutput("&f")+"&a.");

    }
    //

    private void core(Player player){
        AttributeInstance attrHp = player.getAttribute(Attribute.MAX_HEALTH);
        if(attrHp==null){
            senderMessageNP("&8&l> &c&lError~ vida máxima de "+player.getName()+" no está definida.");
            return;
        }
        double maxHp = attrHp.getValue();

        player.setHealth(maxHp);
        U.targetMessageNP(player, "&8&l> &7Te han regenerado tu vida al máximo.");
    }

    //
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("heal")
                .requires(ctx -> ctx.getSender().hasPermission("DottUtils.heal"))
                .then(io.papermc.paper.command.brigadier.Commands.argument("players", ArgumentTypes.players())
                        .executes(ctx->{
                            new Heal(ctx, getPlayerListFromCtx(ctx));
                            return 1;
                        })
                )
                .executes(ctx->{
                    new Heal(ctx, ctx.getSource().getSender());
                    return 1;
                })
                //
                ;
    }
}
