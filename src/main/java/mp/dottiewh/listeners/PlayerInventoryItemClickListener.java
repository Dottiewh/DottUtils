package mp.dottiewh.listeners;

import mp.dottiewh.cinematics.CinematicsConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PlayerInventoryItemClickListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        CinematicsConfig.onItemClick(event);
    }
}
