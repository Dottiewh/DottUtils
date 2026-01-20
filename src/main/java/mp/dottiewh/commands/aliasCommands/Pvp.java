package mp.dottiewh.commands.aliasCommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.commands.Commands;
import mp.dottiewh.config.Config;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class Pvp extends Commands {
    String errorMsg = "&cNo has usado un término correcto.\n&6Posibles usos: &etoggle, status";
    String input;

    public Pvp(CommandContext<CommandSourceStack> ctx, String input) {
        super(ctx);
        this.input=input;

        run();
    }

    @Override
    protected void run(){ //check of what its meaning
        switch (input){
            case "toggle"-> toggle();
            case "status" -> status();

            default -> senderMessage(errorMsg);
        }

    }

    private void status(){
        boolean pvpStatus = Config.getPvPStatus();
        senderMessage("&9El pvp está en: &e"+pvpStatus);
    }
    private void toggle(){
        boolean pvpStatus = Config.getPvPStatus();

        if (pvpStatus) { // si es true, desactiva el pvp
            Config.offPvP();
            senderMessage("&9&lPvP &cDESACTIVADO&9&l.");
        }
        else {// si es false lo activa
            Config.onPvP();
            senderMessage("&9&lPvP &aACTIVADO&9&l.");
        }
    }

    //-------------------------
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("pvp")
                .then(literal("toggle")
                        .executes(ctx->{
                            new Pvp(ctx, "toggle");
                            return 1;
                        })
                )
                .then(literal("status")
                        .executes(ctx->{
                            new Pvp(ctx, "status");
                            return 1;
                        })
                );
    }
}
