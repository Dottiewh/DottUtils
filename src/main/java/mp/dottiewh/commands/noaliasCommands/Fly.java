package mp.dottiewh.commands.noaliasCommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import mp.dottiewh.commands.ReferibleCommand;
import mp.dottiewh.utils.U;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class Fly extends ReferibleCommand {

    List<String> listaNowFlyActivated = new LinkedList<>();
    List<String> listaNowFlyDisactivated = new LinkedList<>();

    public Fly(CommandContext<CommandSourceStack> ctx, CommandSender sender) {
        super(ctx, sender);

        if(isListEmpty) return;
        run();
    }

    public Fly(CommandContext<CommandSourceStack> ctx, List<Player> playerList) {
        super(ctx, playerList);
        if(isListEmpty) return;
        run();
    }

    @Override
    protected void run(){
        for(Player p : playerList){
            boolean newStatus = !p.getAllowFlight();

            if (p.getAllowFlight()){ //== true
                p.setFlying(false);
                p.setAllowFlight(false);
            }
            else{
                p.setAllowFlight(true);
                p.setFlying(true);
            }

            if(newStatus) listaNowFlyActivated.add(p.getName());
            else listaNowFlyDisactivated.add(p.getName());

            String outputFormat = newStatus ? "&a&lActivado!" : "&c&lDesactivado!";
            U.targetMessageNP(p, "&8&l> &aTe han cambiado el vuelo a "+outputFormat);
        }
        String listFormatTrue = String.join("&8, &f", listaNowFlyActivated);
        String listFormatFalse = String.join("&8, &f", listaNowFlyDisactivated);

        if(!listFormatTrue.isEmpty()) senderMessageNP("&8&l> &eLe has &aactivado &eel vuelo a &f"+listFormatTrue);
        if(!listFormatFalse.isEmpty()) senderMessageNP("&8&l> &eLe has &cdesactivado &eel vuelo a &f"+listFormatFalse);
    }

    //
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("fly")
                .requires(ctx -> ctx.getSender().hasPermission("DottUtils.fly"))
                .then(io.papermc.paper.command.brigadier.Commands.argument("players", ArgumentTypes.players())
                        .executes(ctx->{
                            new Fly(ctx, getPlayerListFromCtx(ctx));
                            return 1;
                        })
                )
                .executes(ctx->{
                    new Fly(ctx, ctx.getSource().getSender());
                    return 1;
                })
                //
                ;
    }
}
