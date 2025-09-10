package mp.dottiewh.aliasCommands;

import mp.dottiewh.Commands;
import mp.dottiewh.utils.U;
import mp.dottiewh.config.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.List;
import java.util.Set;

public class Whitelist extends Commands {
    private static String errorMsg = "&cNo has usado un término correcto.\n&6Posibles usos: &eadd, remove, list, toggle, status";
    String nameInput;

    public Whitelist(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);

        run();
    }

    @Override
    protected void run(){ //check of what its meaning
        if (args.length<2){
            senderMessage(errorMsg);
            return;
        }
        switch (args[1]){
            case "add"-> add();
            case "remove" -> remove();
            case "list" -> list();
            case "toggle" -> toggle();
            case "status" -> status();

            default -> senderMessage(errorMsg);
        }

    }

    private void status(){
        boolean status = Config.getWhiteListStatus();
        senderMessage("&9La whitelist está en: &e"+status);
    }
    private void toggle(){
        boolean status = Config.getWhiteListStatus();

        if (status) { // si es true, desactiva la whitelist
            Config.offWhitelist();
            senderMessage("&9&lWhitelist &cDESACTIVADA&9&l.");
        }
        else {// si es false la activa
            Config.onWhitelist();
            senderMessage("&9&lWhitelist &aACTIVADA&9&l.");
        }
    }

    private void add(){
        if (!checkOfUser()) return;

        Config.addWhitelist(nameInput);
        senderMessage("&aHas añadido a &f"+nameInput+"&a a la lista de &fBlanca&a!");
    }
    private  void remove(){
        if (!checkOfUser()) return;

        Config.removeWhitelist(nameInput);
        senderMessage("&cHas removido a &f"+nameInput+"&c de la lista de &fBlanca&c!");
    }
    private void list(){
        List<String> Listablancos = Config.getWhitelist();
        String blancos = String.join(", ", Listablancos);
        senderMessage("&9Lista de whitelisteados: &f"+blancos);
    }

    //-------------------------
    private boolean checkOfUser(){
        if (args.length<3){
            senderMessage("&cPor favor añade un nombre.");
            return false;
        }

        this.nameInput=args[2];
        return true;
    }

    public static void checkWhitelist(AsyncPlayerPreLoginEvent event){
        String name = event.getName();

        if (!Config.getWhiteListStatus()) return; // return if off.
        if (Config.containsAdmin(name)) return;
        if (Config.containsWhitelist(name)) return;

        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, U.mensajeConColor("&cNo estás whitelisteado!"));
    }
}
