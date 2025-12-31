package mp.dottiewh.aliasCommands;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.Commands;
import mp.dottiewh.config.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class Reload extends Commands {
    public Reload(CommandContext<CommandSourceStack> ctx) {
        super(ctx);
        run();
    }

    protected void run(){

        Config.configReload();
        senderMessage("&a&lHas recargado las configuraciones correctamente!");
    }
}
