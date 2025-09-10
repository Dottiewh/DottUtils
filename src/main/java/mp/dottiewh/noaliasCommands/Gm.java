package mp.dottiewh.noaliasCommands;

import mp.dottiewh.Commands;
import mp.dottiewh.utils.U;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class Gm extends Commands {

    public Gm(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);

        run();
    }

    @Override
    protected void run(){
        String errorMsg = "&c&lNo has introducido un valor valido.\n&6&lPosibles valores: &e0, 1, 2, 3 &8| &e&oNombre de un jugador";
        String sucMsg = "&aHas cambiado tu modo de juego a: &f";

        int input;
        boolean toOther = false;

        if (!(sender instanceof Player)) { //determina si es una consola sin especificar un jugador
            if (args.length<2){
                senderMessage("&c&lEste comando solo lo puede usar un jugador. &e&o(A menos que especifiques a uno)");
                return;
            }
        }
        if (args.length==0){ //Determina si no se introdució un valor
            senderMessage(errorMsg);
            return;
        }
        try{ //Determina si el valor fue invalido
            input = Integer.parseInt(args[0]);
        } catch (Exception e){
            senderMessage(errorMsg);
            return;
        }
        //checks to avoid bugs
        Player player;
        if (sender instanceof Player) player = (Player) sender;
        else player = Bukkit.getPlayer(args[1]);

        //checks if the command was for other player
        if (args.length>1){
            player = Bukkit.getPlayer(args[1]);
            toOther = true;
        }
        if (player==null){
            senderMessage("&c&lHas introducido un jugador no conectado.");
            return;
        }
        //------end of checks

        switch (input){
            case 0 -> {
                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage(U.mensajeConPrefix(sucMsg+"Supervivencia"));
            }
            case 1 -> {
                player.setGameMode(GameMode.CREATIVE);
                player.sendMessage(U.mensajeConPrefix(sucMsg+"Creativo"));
            }
            case 2 -> {
                player.setGameMode(GameMode.ADVENTURE);
                player.sendMessage(U.mensajeConPrefix(sucMsg+"Aventura"));
            }
            case 3 -> {
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage(U.mensajeConPrefix(sucMsg+"Espectador"));
            }

            default -> {
                senderMessage(errorMsg);
                return;
            }
        }

        if (toOther){
            senderMessage("&a&lHas cambiado el modo de juego a: &f"+player.getName()+" &8| &e(Input: "+args[0]+")");
        }
    }
}
