package mp.dottiewh;

import mp.dottiewh.items.ItemMainCommand;
import mp.dottiewh.utils.U;
import mp.dottiewh.noaliasCommands.backcore.BackCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import  mp.dottiewh.noaliasCommands.*;
import mp.dottiewh.aliasCommands.*;

import java.util.*;

public abstract class Commands {
    protected Set<String> comandosRegistrados;
    protected CommandSender sender;
    protected Command command;
    protected String label;
    protected String[] args;
    protected Plugin plugin;


    protected Commands (Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        this.sender = sender;
        this.command = command;
        this.label = label;
        this.args = args;
        this.comandosRegistrados = comandosRegistrados;
        this.plugin = JavaPlugin.getProvidingPlugin(getClass());

    }

    public static void commandCore(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args){
        String cmdString = command.getName();
        if (!comandosRegistrados.contains(cmdString)){
            sender.sendMessage(U.mensajeConPrefix(U.getMsgPath("not_right_command"))); //"&c&lNo se ha encontrado tu comando."
            return;
        }


        switch (cmdString.toLowerCase()){
            case "gm"-> new Gm(comandosRegistrados, sender, command, label, args);
            case "jump" -> new Jump(comandosRegistrados, sender, command, label, args);
            case "status" -> new Status(comandosRegistrados, sender, command, label, args);
            case "back" -> new BackCommand(comandosRegistrados, sender, command, label, args);
            case "coords", "coordenadas", "coord", "antonia" -> new Coordenadas(comandosRegistrados, sender, command, label, args);
            case "adminchat", "ac", "achat" -> new AdminChat(comandosRegistrados, sender, command, label, args, true);
            case "dottutils", "du", "dutils" -> checkAllias(comandosRegistrados, sender, command, label, args);

            default -> sender.sendMessage(U.mensajeConPrefix(U.getMsgPath("non_registered_command"))); //"&c&lComando no registrado."
        }

//
    }
    private static void checkAllias(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args){

        if (args.length<1){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',U.getMsgPath("non_registered_command")));
            new Help(comandosRegistrados, sender, command, label, args);
            return;
        }
        String input = args[0].toLowerCase();
        switch (input){
            case "admin", "adm" -> new Admin(comandosRegistrados, sender, command, label, args);
            case "reload" -> new Reload(comandosRegistrados, sender, command, label, args);
            case "help", "-h", "--help" -> new Help(comandosRegistrados, sender, command, label, args);
            case "adminchat", "ac" -> new AdminChat(comandosRegistrados, sender, command, label, args, false);
            case "whitelist", "wl" -> new Whitelist(comandosRegistrados, sender, command, label, args);
            case "pvp" -> new Pvp(comandosRegistrados, sender, command, label, args);
            case "nofall", "nf" -> new NoFall(comandosRegistrados, sender, command, label, args);
            case "item"-> new ItemMainCommand(comandosRegistrados, sender, command, label, args);

            default -> {
                sender.sendMessage(U.mensajeConPrefix("&c&lSub-índice no encontrado."));
                sender.sendMessage(U.mensajeConPrefix("&6Puedes probar usando &f/du help&6!"));
            }
        }
    }


    // comandos como tal
    protected abstract void run();


    // metodos utiles
    protected void senderMessage(String mensaje){
        sender.sendMessage(U.mensajeConPrefix(mensaje));
    }
    protected void senderMessageNP(String mensaje){
        sender.sendMessage(U.mensajeConColor(mensaje));
    }
}
