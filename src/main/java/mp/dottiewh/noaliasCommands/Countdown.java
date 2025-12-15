package mp.dottiewh.noaliasCommands;

import mp.dottiewh.Commands;
import mp.dottiewh.utils.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class Countdown extends Commands {
    int segundos;

    public Countdown(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);

        if(args.length==0){
            senderMessageNP("&cIntroduce un número de segundos validos o pon stop.");
            return;
        }
        if(args[0].equalsIgnoreCase("stop")){
            runStop();
            return;
        }

        try{
            this.segundos = Integer.parseInt(args[0]);
        } catch (Exception e) {
            senderMessageNP("&cNo se ha podido convertir "+args[0]+" a segundos.");
            return;
        }


        run();
    }

    @Override
    protected void run() {
        U.countdownForAll(plugin, segundos, "&7Segundos restantes: &f");
        senderMessageNP("&aSe ha emitido la orden correctamente! &e("+segundos+")");
    }
    private void runStop(){
        senderMessageNP("&aSe han parado cualquier cuenta atrás existente.");
        U.stopAllCountdowns();
    }
}
