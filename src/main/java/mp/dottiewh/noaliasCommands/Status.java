package mp.dottiewh.noaliasCommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import mp.dottiewh.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.springframework.expression.spel.ast.Literal;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static io.papermc.paper.command.brigadier.Commands.argument;

public class Status extends Commands {

    public Status(CommandContext<CommandSourceStack> ctx) {
        super(ctx);

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
            else tpsD[i]="&4"; // 0-10 = rojo oscuro

            tpsD[i] = tpsD[i]+df.format(tps[i]);
        }

        //format of ping

        senderMessageNP("&8&l----&6Rendimiento&8&l----");
        senderMessageNP("&6Tps: "+String.join(", ", tpsD)+" &e&o(1m, 5m, 15m)");
        senderMessageNP("&6Ram: &e"+usedMemory+" MB &f/&e "+allocatedMemory+" MB &8| &9&o(Real Max: &f&o"+maxMemory+" MB&9&o)");
        senderMessageNP("&6Jugadores: &a"+onlinePlayers+"&f/&a"+maxPlayers);
        if(sender instanceof Player player){
            int ping = player.getPing();
            String pingD = "";

            if (ping<51) pingD="&a"; // 0-50 = verde
            else if (ping<121) pingD="&e"; // 51-120 = amarillo
            else if (ping<226) pingD="&c"; // 121-225 = rojo
            else pingD="&4"; // 225+ = rojo oscuro
            pingD = pingD+ping;
            senderMessageNP("&6Tu ping: "+pingD+" &8| &e&oavg: "+getAvgPing());
        }else{
            if(!Bukkit.getOnlinePlayers().isEmpty()){
                senderMessageNP("&6Ping promedio: &e"+getAvgPing());
            }
        }
    }
    private double getAvgPing(){
        List<Player> listaJugadores = new ArrayList<>(Bukkit.getOnlinePlayers());
        double ping = 0;
        for(Player player : listaJugadores){
            ping = ping + player.getPing();
        }
        return ping / listaJugadores.size();
    }

    //
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return io.papermc.paper.command.brigadier.Commands.literal("status")
                .executes(ctx->{
                    new Status(ctx);
                    return 1;
                })
                //
                ;
    }
}
