package mp.dottiewh.listeners;

import mp.dottiewh.utils.ItemUtils;
import mp.dottiewh.utils.U;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityAttackListener implements Listener {

    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent event){
        U.noPvP(event);
        ItemUtils.checkItemAttackCustomData(event);
    }
}
