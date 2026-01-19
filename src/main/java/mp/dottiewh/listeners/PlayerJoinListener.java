package mp.dottiewh.listeners;

import mp.dottiewh.cinematics.CinematicsConfig;
import mp.dottiewh.commands.noaliasCommands.playtimecore.PlayTimeManagement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        PlayTimeManagement.onJoinManagement(event);
        CinematicsConfig.onJoinCheck(event.getPlayer());
    }
}
