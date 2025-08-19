package mp.dottiewh.aliasCommands;

import mp.dottiewh.Commands;
import mp.dottiewh.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class Reload extends Commands {
    public Reload(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);

        run();
    }

    protected void run(){
        U.configReload();
        senderMessage("&a&lHas recargado las configuraciones correctamente!");
    }
}
