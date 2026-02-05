package mp.dottiewh.listeners.entity;

import mp.dottiewh.utils.ItemUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        ItemUtils.checkItemDeathCustomData(event);
    }
}
