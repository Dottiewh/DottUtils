package mp.dottiewh.listeners.entity;

import mp.dottiewh.utils.ItemUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

public class EntityBowShotListener implements Listener {
    @EventHandler
    public void onEntityShot(EntityShootBowEvent event){
        ItemUtils.checkItemBowShotCustomData(event);
    }
}
