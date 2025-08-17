package mp.dottiewh;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class Commands {
    private Set<String> comandosRegistrados;
    private CommandSender sender;
    private Command command;
    private String label;
    private String[] args;


    public Commands (Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        this.sender = sender;
        this.command = command;
        this.label = label;
        this.args = args;
        this.comandosRegistrados = comandosRegistrados;

        commandCore();
    }

    private void commandCore(){
        String cmdString = command.getName();
        if (!comandosRegistrados.contains(cmdString)){
            sender.sendMessage(U.mensajeConPrefix("&c&lNo se ha encontrado tu comando."));
            return;
        }

        switch (cmdString.toLowerCase()){
            case "gm"-> gm();


            default -> sender.sendMessage(U.mensajeConPrefix("&c&lComando no registrado."));
        }


    }

    // comandos como tal
    private void gm(){
        String errorMsg = "&c&lNo has introducido un valor valido.\n&6&lPosibles valores: &e0, 1, 2, 3";
        String sucMsg = "&a&lHas cambiado tu modo de juego a: &f";

        int input;
        boolean toOther = false;

        if (!(sender instanceof Player player)) {
            senderMessage("&c&lEste comando solo lo puede usar un jugador.");
            return;
        }

        if (args.length==0){
            senderMessage(errorMsg);
            return;
        }
        try{
            input = Integer.parseInt(args[0]);
        } catch (Exception e){
            senderMessage(errorMsg);
            return;
        }

        //------end of checks
        if (args.length>1){
            player = Bukkit.getPlayer(args[1]);

            if (player==null){
                senderMessage("&c&lHas introducido un jugador no conectado.");
                return;
            }

            toOther = true;
        }

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
            senderMessage("&a&lHas cambiado el modo de juego a: &f"+player.getName()+" &8| &e(Input: "+args[1]+")");
        }
    }


    // metodos utiles
    public void senderMessage(String mensaje){
        sender.sendMessage(U.mensajeConPrefix(mensaje));
    }
}
