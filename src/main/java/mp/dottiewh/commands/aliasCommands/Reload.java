package mp.dottiewh.commands.aliasCommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.commands.Commands;
import mp.dottiewh.config.Config;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class Reload extends Commands {
    public Reload(CommandContext<CommandSourceStack> ctx) {
        super(ctx);
        run();
    }

    protected void run(){

        Config.configReload();
        senderMessage("&a&lHas recargado las configuraciones correctamente!");
    }

    //----------------------------------
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("reload")
                .executes(ctx->{
                    new Reload(ctx);
                    return 1;
                });
    }
}
