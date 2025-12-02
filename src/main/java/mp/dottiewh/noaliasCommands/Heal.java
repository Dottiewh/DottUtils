package mp.dottiewh.noaliasCommands;

import mp.dottiewh.Commands;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class Heal extends Commands {
    public Heal(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);

        run();
    }

    @Override
    protected void run() {
        if (!(sender instanceof Player player)){
            senderMessageNP("&cEste comando solo lo puede usar un jugador.");
            return;
        }

        

        AttributeInstance attrHp = player.getAttribute(Attribute.MAX_HEALTH);
        if(attrHp==null){
            senderMessageNP("&8&l> &c&lError~ Tu vida máxima no está definida.");
            return;
        }
        double maxHp = attrHp.getValue();

        player.setHealth(maxHp);
        senderMessageNP("&8&l> &aHas regenerado tu vida correctamente. &e("+maxHp+")");
    }
    //
    private void forOther(){

    }
}
