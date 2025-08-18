package mp.dottiewh;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import  mp.dottiewh.noaliasCommands.*;

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
            sender.sendMessage(U.mensajeConPrefix("&c&lNo se ha encontrado tu comando."));
            return;
        }

        switch (cmdString.toLowerCase()){
            case "gm"-> new Gm(comandosRegistrados, sender, command, label, args);
            case "jump" -> new Jump(comandosRegistrados, sender, command, label, args);

            default -> sender.sendMessage(U.mensajeConPrefix("&c&lComando no registrado."));
        }


    }

    // comandos como tal
    protected abstract void run();


    // metodos utiles
    protected void senderMessage(String mensaje){
        sender.sendMessage(U.mensajeConPrefix(mensaje));
    }
}
