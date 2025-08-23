package mp.dottiewh.noaliasCommands;

import mp.dottiewh.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.text.DecimalFormat;

import java.util.Set;

public class Status extends Commands {
    public Status(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);

        run();
    }

    @Override
    protected void run() {
        double[] tps = Bukkit.getServer().getTPS();
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024*1024); // en mb
        long allocatedMemory = Runtime.getRuntime().totalMemory() / (1024*1024); // en mb
        //long freeMemory = Runtime.getRuntime().freeMemory() / (1024*1024*1024);
        long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024); // en mb
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int maxPlayers = Bukkit.getMaxPlayers();
        DecimalFormat df = new DecimalFormat("#.##");

        String[] tpsD = new String[3];
        for (int i=0; i<3; i++){
            if (tps[i]>=18) tpsD[i]="&a"; // 18-20 = verde
            else if (tps[i]>=15) tpsD[i]="&e"; // 15-18 = amarillo
            else if (tps[i]>=10) tpsD[i]="&c"; // 10-15 = rojo
            else tpsD[i]="&c"; // 0-10 = rojo oscuro

            tpsD[i] = tpsD[i]+df.format(tps[i]);
        }

        senderMessageNP("&8&l----&6Rendimiento&8&l----");
        senderMessageNP("&6Tps: "+String.join(", ", tpsD)+" &e&o(1m, 5m, 15m)");
        senderMessageNP("&6Ram: &e"+usedMemory+" MB &f/&e "+allocatedMemory+" MB &8| &9&o(Real Max: &f&o"+maxMemory+" MB&9&o)");
        senderMessageNP("&eJugadores: &a"+onlinePlayers+"&f/&a"+maxPlayers);
    }
}
