package mp.dottiewh.commands.aliasCommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.commands.Commands;
import mp.dottiewh.config.Config;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class NoFall extends Commands {
    String errorMsg = "&cNo has usado un término correcto.\n&6Posibles usos: &etoggle, status";
    String input;

    public NoFall(CommandContext<CommandSourceStack> ctx, String input) {
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
        boolean noFallStatus = Config.getNoFallStatus();
        boolean realNFS = !noFallStatus;

        senderMessage("&9El daño de caída está en: &e"+realNFS+" &e&o("+noFallStatus+")");
    }
    private void toggle(){
        boolean noFallStatus = Config.getNoFallStatus();

        if (noFallStatus) { // si es true, desactiva no fall
            Config.offNoFall();
            senderMessage("&9&lDaño de caída &aACTIVADO&9&l.");
        }
        else {// si es false lo activa
            Config.onNoFall();
            senderMessage("&9&lDaño de caída &cDESACTIVADO&9&l.");
        }
    }

    //----------------------------------
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("nofall")
                .then(literal("toggle")
                        .executes(ctx->{
                            new NoFall(ctx, "toggle");
                            return 1;
                        })
                )
                .then(literal("status")
                        .executes(ctx->{
                            new NoFall(ctx, "status");
                            return 1;
                        })
                );
    }
}
