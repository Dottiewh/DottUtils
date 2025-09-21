package mp.dottiewh.noaliasCommands.tpacore;

import mp.dottiewh.Commands;
import mp.dottiewh.config.Config;
import mp.dottiewh.utils.U;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class Tpa extends Commands {


    public Tpa(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);

        if (TpaCore.failedGlobalTpaChecks(sender, args)) return;
        run();
    }

    @Override
    protected void run() {
        String input = args[0];
        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(input);
        if(target==null){
            TpaCore.senderMsgPr("&cEl jugador &f"+input+" &cno está conectado.", player);
            return;
        }
        TpaCore.addTpRequest(player.getName(), input, plugin);
    }
}
