package mp.dottiewh.listeners.player;

import mp.dottiewh.cinematics.CinematicsConfig;
import mp.dottiewh.music.MusicFront;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PlayerInventoryItemClickListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        CinematicsConfig.onItemClick(event);
        MusicFront.onInvClick(event);
    }
}
