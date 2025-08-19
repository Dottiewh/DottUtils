package mp.dottiewh.aliasCommands;

import mp.dottiewh.Commands;
import mp.dottiewh.DottUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class Help extends Commands {
    // prefix sin corchetes ni espacios
    private static final String prefixBellaco = DottUtils.prefix.replace("[", "").replace("]", "").replace(" ", "");

    public Help(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);

        run();
    }

    @Override
    protected void run(){

        senderMessage("&8&l--------------"+prefixBellaco+"&8&l-----------------------");
        msg1();

    }

    private void msg1(){
        senderMessage("&8&lComando: &f/du admin &f&o[adm]");
        senderMessage("&7&lUsos: &eadd, remove, list");
        senderMessage("&4-----");
        senderMessage("&8&lComando: &f/du reload");
        senderMessage("&7&lDescripción: &eRecarga las configuraciones.");

        //
        senderMessage("&8&l-&f&l1/?&8&l------------------------------------------");
    }
}
