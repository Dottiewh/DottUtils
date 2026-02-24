package mp.dottiewh.commands.noaliasCommands.tpacore;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import mp.dottiewh.commands.Commands;
import org.bukkit.entity.Player;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class Tpa extends Commands {
    Player player;

    public Tpa(CommandContext<CommandSourceStack> ctx, boolean target) {
        super(ctx, target);
        if(!allGood) return;
        if(TpaCore.failedGlobalTpaChecks(sender)) return;
        this.player = (Player) sender;

        run();
    }

    @Override
    protected void run() {
        if(player.getUniqueId().equals(classTarget.getUniqueId())){
            TpaCore.senderMsgPr("&cNo puedes enviarte un tpa a ti mismo!", sender);
            return;
        }
        Player target = classTarget;
        String tName = target.getName();

        if(checkIfItHasATpa()) TpaCore.tpacancel(player.getName());

        TpaCore.addTpRequest(player.getName(), tName, plugin);
    }
    private boolean checkIfItHasATpa() {
        for (String key : TpaCore.hashMap.keySet()) {
            String whoSentTpa = key.split(";")[0];
            if (whoSentTpa.equals(player.getName())) return true;
        }
        return false;
    }

    //
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("tpa")
                .then(io.papermc.paper.command.brigadier.Commands.argument("player", ArgumentTypes.player())
                        .executes(ctx -> {
                            new Tpa(ctx, true);
                            return 1;
                        })
                )
                //
                ;
    }

}
