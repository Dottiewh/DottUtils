package mp.dottiewh.noaliasCommands;

import mp.dottiewh.Commands;
import mp.dottiewh.utils.U;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class Heal extends Commands {
    boolean forOther;

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

        if(args.length == 0){
            core(player);
        }

        Player rP = checkIfForOtherPlayerP(args[0], null);
        if (rP==null){
            senderMessageNP("&cNo se ha encontrado al jugador "+args[0]);
            return;
        }
        this.forOther = checkIfForOtherPlayer(args[0]);
        core(rP);
    }
    //
    private void forOther(){

    }
    private void core(Player player){
        AttributeInstance attrHp = player.getAttribute(Attribute.MAX_HEALTH);
        if(attrHp==null){
            senderMessageNP("&8&l> &c&lError~ vida máxima no está definida.");
            return;
        }
        double maxHp = attrHp.getValue();

        player.setHealth(maxHp);
        U.targetMessageNP(player, "&8&l> &7Te han regenerado tu vida al máximo.");
        if(forOther) senderMessageNP("&8&l> &aSe le ha regenerado la vida correctamente a "+player.getName()+". &e("+maxHp+")");
    }
}
