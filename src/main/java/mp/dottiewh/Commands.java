package mp.dottiewh;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.*;

public class Commands {
    private Set<String> comandosRegistrados;
    private CommandSender sender;
    private Command command;
    private String label;
    private String[] args;
    private Plugin plugin;


    public Commands (Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        this.sender = sender;
        this.command = command;
        this.label = label;
        this.args = args;
        this.comandosRegistrados = comandosRegistrados;
        this.plugin = JavaPlugin.getProvidingPlugin(getClass());

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
            case "jump" -> jump();

            default -> sender.sendMessage(U.mensajeConPrefix("&c&lComando no registrado."));
        }


    }

    // comandos como tal


    private void jump() {
        String errorMsg = "&c&lNo has introducido un valor valido.\n&6&lPosibles valores: &eNúmero | &e&oNombre de un jugador &8| &e&oNúmero";
        String sucMsg = "&a&lHas cambiado tu modo de juego a: &f";
        int times = 1;
        double input;
        boolean toOther = false;

        if (!(sender instanceof Player)) { //determina si es una consola sin especificar un jugador
            if (args.length < 2) {
                senderMessage("&c&lEste comando solo lo puede usar un jugador. &e&o(A menos que especifiques a uno)");
                return;
            }
        }
        if (args.length == 0) { //determina si no se especificó un impulso
            senderMessage(errorMsg);
            return;
        }
        try { //Determina si no se introdució un impulso apto (no double)
            input = Double.parseDouble(args[0]);
        } catch (Exception e) {
            senderMessage(errorMsg);
            return;
        }
        Player player;
        //checks of bugs
        if (sender instanceof Player) player = (Player) sender;
        else player = Bukkit.getPlayer(args[1]);

        // searching for times
        if (args.length > 1) {
            try {
                times = Integer.parseInt(args[1]);
            } catch (Exception e) {
                if (args.length > 2) {
                    try {
                        times = Integer.parseInt(args[2]);
                    } catch (Exception e2) {
                        System.out.println("well");
                    }
                }
            }
        }
        //just checking if the command was for another player
        if (args.length > 1) {
            player = Bukkit.getPlayer(args[1]);
            toOther = true;

        }
        if (player == null) {
            if (!(sender instanceof Player && times != 1)) {
                senderMessage("&c&lHas introducido un jugador no conectado.");
                return;
            }
            else {toOther = false; player = (Player) sender;}
        }

        if (times > 50) {senderMessage("&cBro controlate &e(max 50)&c."); return;}
        //------end of checks
        Player target = player;

        for (int i = 0; i < times; i++) {
            int delay = 5 * i;

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                target.setVelocity(new Vector(0, input, 0));
            }, delay);

        }


        if (toOther){
            senderMessage("&a&lHas hecho saltar a: &f"+player.getName()+" &8| &e(Input: "+args[0]+") &7("+times+")");
        }
    }

    //GM or gamemode
    private void gm(){
        String errorMsg = "&c&lNo has introducido un valor valido.\n&6&lPosibles valores: &e0, 1, 2, 3 &8| &e&oNombre de un jugador";
        String sucMsg = "&a&lHas cambiado tu modo de juego a: &f";

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


    // metodos utiles
    public void senderMessage(String mensaje){
        sender.sendMessage(U.mensajeConPrefix(mensaje));
    }
}
