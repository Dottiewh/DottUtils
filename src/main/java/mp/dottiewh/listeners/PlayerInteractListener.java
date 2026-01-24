package mp.dottiewh.listeners;

import mp.dottiewh.cinematics.CinematicsConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        CinematicsConfig.onItemInteract(event);
    }
}
