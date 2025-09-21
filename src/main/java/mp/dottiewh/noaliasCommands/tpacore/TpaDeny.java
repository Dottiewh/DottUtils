package mp.dottiewh.noaliasCommands.tpacore;

import mp.dottiewh.Commands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class TpaDeny extends Commands {
    public TpaDeny(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);

        if(TpaCore.failedGlobalTpaChecks(sender)) return;
        run();
    }

    @Override
    protected void run() {
        Player player = (Player) sender;
        TpaCore.tpadeny(player.getName());
    }
}
