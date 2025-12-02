package mp.dottiewh.noaliasCommands;

import mp.dottiewh.Commands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Set;

public class Repair extends Commands {
    public Repair(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);
        run();
    }

    @Override
    protected void run() {
        if(!(sender instanceof Player player)){
            senderMessageNP("&cEste comando solo lo puede usar un jugador.");
            return;
        }
        ItemStack itemS = player.getInventory().getItemInMainHand();
        ItemMeta meta = itemS.getItemMeta();

        if(itemS.equals(ItemStack.empty())){
            senderMessageNP("&8&l> &cNo tienes ningún item en tu mano principal.");
            return;
        }

        int maxDuration = itemS.getMaxItemUseDuration(player); // no necesario al final lol

        maxDuration = 0;

        Damageable dmg = (Damageable) meta;
        dmg.setDamage(maxDuration);

        itemS.setItemMeta(dmg);
        senderMessageNP("&8&l> &aHas recuperado correctamente la durabilidad del item en tu mano principal! &e("+maxDuration+")");
    }
}
