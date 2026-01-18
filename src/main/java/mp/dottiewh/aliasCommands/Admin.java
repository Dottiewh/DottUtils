package mp.dottiewh.aliasCommands;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.Commands;
import mp.dottiewh.config.Config;

import java.util.List;

public class Admin extends Commands {
    String errorMsg = "&cNo has usado un término correcto.\n&6Posibles usos: &eadd, remove, list";
    String type;
    String name;

    public Admin(CommandContext<CommandSourceStack> ctx, String type, String name) {
        super(ctx);
        this.type=type;
        this.name=name;

        run();
    }


    @Override
    protected void run(){ //check of what its meaning

        switch (type){
            case "add"-> add();
            case "remove" -> remove();
            case "list" -> list();

            default -> senderMessage(errorMsg);
        }

    }

    private void add(){
        Config.addAdmin(name);
        senderMessage("&aHas añadido a &f"+name+"&a a la lista de &9Admins&a!");
    }
    private  void remove(){
        Config.removeAdmin(name);
        senderMessage("&cHas removido a &f"+name+"&c de la lista de &9Admins&a!");
    }
    private void list(){
        List<String> admins = Config.getAdminList();
        String listaAdminsJoin = String.join(", ", admins);
        senderMessage("&aLista de admins: &f"+listaAdminsJoin);
    }
}
