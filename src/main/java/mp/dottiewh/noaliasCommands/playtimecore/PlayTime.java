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
        try{
            List<Map.Entry<String, Integer>> lista = PlayTimeManagement.getTop(limit);
            if(lista.isEmpty()) throw new NoPlaytimesException();

            senderMessageNP("&8-----&6~&c&lTop&6~&8-----");
            for (Map.Entry<String, Integer> entry : lista) {
                String dKey = entry.getKey();
                if(dKey.equals(PlayTimeManagement.serverName)) dKey="&6"+dKey;

                senderMessageNP("&b" + dKey + "&7: &e" + format(entry.getValue()));
            }

            if (askingFor == null) return;
            senderMessageNP("&8--");
            senderMessageNP("&8( &a" + askingFor + "&8 )");
            senderMessageNP("&8&lTiempo de juego: &7~&e" + format(PlayTimeManagement.getNameValue(askingFor))+"&7~");
        }catch(NoPlaytimesException e) {
            senderMessageNP("&cNo hay ningún tiempo de juego guardado.");
        }
    }
    //
    private String format(int v){
        boolean hours = (v>=60);
        boolean days = (v>=1440);
        boolean months = (v>=43800);
        boolean years = (v>=525600);

        int localCount = v;
        int lYears=0, lMonths=0, lDays=0, lHours=0;
        while(localCount>=525600){
            localCount=localCount-525600;
            lYears++;
        }
        while(localCount>=43800){
            localCount=localCount-43800;
            lMonths++;
        }
        while(localCount>=1440){
            localCount=localCount-1440;
            lDays++;
        }
        while(localCount>=60){
            localCount=localCount-60;
            lHours++;
        }
        int lMinutes=localCount;

        if(years){
            return(lYears+"y, "+lMonths+"M, "+lDays+"d, "+lHours+"h, "+lMinutes+"m.");
        }
        if(months){
            return(lMonths+"M, "+lDays+"d, "+lHours+"h, "+lMinutes+"m.");
        }
        if(days){
            return(lDays+"d, "+lHours+"h, "+lMinutes+"m.");
        }
        if(hours){
            return(lHours+"h, "+lMinutes+"m.");
        }
        return(lMinutes+"m.");
    }
}
