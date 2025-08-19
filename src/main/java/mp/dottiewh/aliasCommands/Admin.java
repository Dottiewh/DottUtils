package mp.dottiewh.aliasCommands;

import mp.dottiewh.Commands;
import mp.dottiewh.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Set;

public class Admin extends Commands {
    String errorMsg = "&cNo has usado un término correcto.\n&6Posibles usos: &eadd, remove, list";
    String nameInput;

    public Admin(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
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

            default -> senderMessage(errorMsg);
        }

    }

    private void add(){
        if (!checkOfUser()) return;

        U.addAdmin(nameInput);
        senderMessage("&aHas añadido a &f"+nameInput+"&a a la lista de &9Admins&a!");
    }
    private  void remove(){
        if (!checkOfUser()) return;

        U.removeAdmin(nameInput);
        senderMessage("&cHas removido a &f"+nameInput+"&c de la lista de &9Admins&a!");
    }
    private void list(){
        List<String> admins = U.getAdminList();
        String listaAdminsJoin = String.join(", ", admins);
        senderMessage("&aLista de admins: &f"+listaAdminsJoin);
    }

    private boolean checkOfUser(){
        if (args.length<3){
            senderMessage("&cPor favor añade un nombre.");
            return false;
        }

        this.nameInput=args[2];
        return true;
    }
}
