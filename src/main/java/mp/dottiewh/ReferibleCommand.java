package mp.dottiewh;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReferibleCommand extends Commands{
    public List<Player> playerList;
    public boolean isListEmpty=false;

    public ReferibleCommand(CommandSender sender, String input) {
        super(sender, input);
    }

    public ReferibleCommand(CommandContext<CommandSourceStack> ctx, boolean target) {
        super(ctx, target);
    }

    public ReferibleCommand(CommandContext<CommandSourceStack> ctx) {
        super(ctx);
    }
    //
    public ReferibleCommand(CommandSender sender, String input, List<Player> playerList) {
        super(sender, input);
        this.playerList=playerList;
        commonCheck();
    }

    public ReferibleCommand(CommandContext<CommandSourceStack> ctx, boolean target, List<Player> playerList) {
        super(ctx, target);
        this.playerList=playerList;
        commonCheck();
    }

    public ReferibleCommand(CommandContext<CommandSourceStack> ctx, List<Player> playerList) {
        super(ctx);
        this.playerList=playerList;
        commonCheck();
    }
    public ReferibleCommand(CommandContext<CommandSourceStack> ctx, CommandSender sender) {
        super(ctx);
        if(!(sender instanceof Player p)){
            senderMessage("&cEste tipo de referenciación solo la puede usar un jugador!");
            senderMessage("&6Prueba a referirte con @a o a alguien!");
            isListEmpty=true;
            return;
        }
        List<Player> toDeliver = new ArrayList<>();
        toDeliver.add(p);
        this.playerList=toDeliver;
        commonCheck();
    }
    //

    private void commonCheck(){
        if(playerList.isEmpty()){
            senderMessage("&e&lNo te has referido a ningún jugador!");
            isListEmpty=true;
        }
    }

    public List<String> getPlayerNameList(){
        List<String> toGive = new ArrayList<>();
        for(Player p :playerList){
            toGive.add(p.getName());
        }
        return toGive;
    }
    public String getOutput(String formatColor){
        return String.join("&8, "+formatColor, getPlayerNameList());
    }
}
