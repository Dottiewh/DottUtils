package mp.dottiewh.aliasCommands;

import mp.dottiewh.Commands;
import mp.dottiewh.U;
import mp.dottiewh.config.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class Pvp extends Commands {
    String errorMsg = "&cNo has usado un término correcto.\n&6Posibles usos: &etoggle, status";

    public Pvp(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);

        run();
    }

    @Override
    protected void run(){ //check of what its meaning
        if (args.length<2){
            senderMessage(errorMsg);
            return;
        }
        switch (args[1]){
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
}
