package mp.dottiewh.listeners;

import mp.dottiewh.DottUtils;
import mp.dottiewh.cinematics.CinematicsConfig;
import mp.dottiewh.commands.noaliasCommands.backcore.BackUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerItemDropListener implements Listener {
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event){
        CinematicsConfig.onPlayerDrop(event);
    }
}
