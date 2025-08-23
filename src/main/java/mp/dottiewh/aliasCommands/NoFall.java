package mp.dottiewh.aliasCommands;

import mp.dottiewh.Commands;
import mp.dottiewh.config.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class NoFall extends Commands {
    String errorMsg = "&cNo has usado un término correcto.\n&6Posibles usos: &etoggle, status";

    public NoFall(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
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
}
