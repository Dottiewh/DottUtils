package mp.dottiewh.aliasCommands;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.Commands;
import mp.dottiewh.config.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class Admin extends Commands {
    String errorMsg = "&cNo has usado un término correcto.\n&6Posibles usos: &eadd, remove, list";
    String type;

    public Admin(CommandContext<CommandSourceStack> ctx, String type, boolean forTarget) {
        super(ctx, forTarget);
        this.type=type;
        if(allGood)run();
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
        Config.addAdmin(getUserName());
        senderMessage("&aHas añadido a &f"+getUserName()+"&a a la lista de &9Admins&a!");
    }
    private  void remove(){
        Config.removeAdmin(getUserName());
        senderMessage("&cHas removido a &f"+getUserName()+"&c de la lista de &9Admins&a!");
    }
    private void list(){
        List<String> admins = Config.getAdminList();
        String listaAdminsJoin = String.join(", ", admins);
        senderMessage("&aLista de admins: &f"+listaAdminsJoin);
    }

    private String getUserName(){
        if(target==null) return null;

        return target.getName();
    }
}
