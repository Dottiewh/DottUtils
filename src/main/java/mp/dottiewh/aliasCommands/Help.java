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
        int page = 1;
        if (args.length>1){
            try{
                page = Integer.parseInt(args[1]);
            }catch(NumberFormatException e){
                senderMessageNP("&e&oPágina inválida, usando página 1...");
            }
        }

        senderMessageNP("&8&l--------------"+prefixBellaco+"&8&l-----------------------");
        switch (page){
            case 1-> msg1();
            case 2-> msg2();

            default-> senderMessage("&cNo has introducido un índice valido.");
        }

    }

    private void msg1(){
        senderMessageNP("&8&lComando: &f/du admin &f&o[adm]");
        senderMessageNP("&7&lUsos: &eadd, remove, list");
        senderMessageNP("&4-----");
        senderMessageNP("&8&lComando: &f/du reload");
        senderMessageNP("&7&lDescripción: &eRecarga las configuraciones.");
        senderMessageNP("&4-----");
        senderMessageNP("&8&lComando: &f/du adminchat &f&o[ac]");
        senderMessageNP("&7&lUsos: &etoggle, leave, join");
        senderMessageNP("&4-----");
        senderMessageNP("&8&lComando: &f/du whitelist &f&o[wl]");
        senderMessageNP("&7&lUsos: &eadd, remove, list, toggle, status");
        senderMessageNP("&4-----");
        senderMessageNP("&8&lComando: &f/du pvp");
        senderMessageNP("&7&lUsos: &etoggle, status");
        senderMessageNP("&4-----");
        senderMessageNP("&8&lComando: &f/du nofall &f&o[nf]");
        senderMessageNP("&7&lUsos: &etoggle, status");

        //
        senderMessageNP("&8&l-&f&l1/2&8&l---------------------------------------");
    }

    private void msg2(){
        senderMessageNP("&8&lComando: &f/du item");
        senderMessageNP("&7&lUsos: &esave, get, give, delete &e&o[del]&e, list");

        //
        senderMessageNP("&8&l-&f&l2/2&8&l---------------------------------------");
    }
}
