package mp.dottiewh.noaliasCommands.playtimecore;

import mp.dottiewh.Commands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayTime extends Commands {
    boolean isConsole;
    boolean toOtherPlayer;
    String askingFor;

    private static final int limit = 4;

    public PlayTime(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);
        toOtherPlayer=false;

        if(sender instanceof Player player){
            this.askingFor=player.getName();
            isConsole=false;
        }else{
            this.askingFor=null;
            isConsole=true;
        }
        if(args.length>0){
            if(!PlayTimeManagement.isNameInConfig(args[0])){
                senderMessageNP("&cEl nombre &f"+args[0]+" &cno está registrado!");
                return;
            }
            this.askingFor=args[0];
            toOtherPlayer=true;
        }

        run();
    }

    @Override
    protected void run() {
        senderMessageNP("&8-----&6~&9Top&6~&8-----");
        List<Map.Entry<String, Integer>> lista = PlayTimeManagement.getTop(limit);
        for (Map.Entry<String, Integer> entry : lista) {
            senderMessageNP("&9" + entry.getKey() + "&6: &e" + format(entry.getValue()));
        }
        if(askingFor==null) return;

        senderMessageNP("&8----&7(&f"+askingFor+"&7)&8----");
        senderMessageNP("&9Tiempo de juego: &6"+format(PlayTimeManagement.getNameValue(askingFor)));
    }
    //
    private String format(int v){
        boolean hours = false, days = false;
        if(v>=60) hours=true;
        if(v>=1440) days=true;
        int localCount = v;
        int lDays=0, lHours=0, lMinutes=0;
        while(localCount>=1440){
            localCount=localCount-1440;
            lDays++;
        }
        while(localCount>=60){
            localCount=localCount-60;
            lHours++;
        }
        lMinutes=localCount;


        if(days){
            return(lDays+"D, "+lHours+"H, "+lMinutes+"M.");
        }
        if(hours){
            return(lHours+"H, "+lMinutes+"M.");
        }
        return(lMinutes+"M.");
    }
}
