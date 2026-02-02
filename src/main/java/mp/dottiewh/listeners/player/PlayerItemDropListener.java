package mp.dottiewh.listeners.player;

import mp.dottiewh.cinematics.CinematicsConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerItemDropListener implements Listener {
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event){
        CinematicsConfig.onPlayerDrop(event);
    }
}
