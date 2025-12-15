package mp.dottiewh.noaliasCommands;

import mp.dottiewh.Commands;
import mp.dottiewh.utils.U;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class Feed extends Commands {

    public Feed(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);
        run();
    }

    @Override
    protected void run() {
        if (!(sender instanceof Player player)){
            senderMessageNP("&cEste comando solo lo puede usar un jugador.");
            return;
        }
        boolean isForOther;

        if(args.length == 0)isForOther=false;
        else{
            isForOther = checkIfForOtherPlayer(args[0]);
        }

        if (isForOther){
            forOther();
            return;
        }

        player.setFoodLevel(20);
        player.setSaturation(20f);
        senderMessageNP("&8&l> &aHas devuelto tu comida al máximo nivel! &e(20)");
    }
    //
    private void forOther(){
        Player player = Bukkit.getPlayerExact(args[0]);
        if (player==null){
            senderMessageNP("&cNo se ha encontrado al jugador "+args[0]);
            return;
        }

        player.setFoodLevel(20);
        player.setSaturation(20f);
        U.targetMessageNP(player, "&8&l> &eTe han regenerado la comida al máximo.");
    }
}
