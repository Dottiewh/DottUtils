package mp.dottiewh.aliasCommands;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.ReferibleCommand;
import mp.dottiewh.utils.U;
import org.bukkit.entity.Player;

import java.util.List;

public class TellRaw extends ReferibleCommand {
    String text;

    public TellRaw(CommandContext<CommandSourceStack> ctx, String text, List<Player> pList) {
        super(ctx, pList);
        if(isListEmpty) return;

        this.text=text;
        run();
    }
    @Override
    protected void run(){
        for(Player p : playerList){
            U.targetMessageNP(p, text);
        }
        senderMessage("&aLe has mandado un tellraw a &f"+getOutput("&f"));
    }
}
