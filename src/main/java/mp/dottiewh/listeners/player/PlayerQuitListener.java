package mp.dottiewh.listeners.player;

import mp.dottiewh.cinematics.CinematicsConfig;
import mp.dottiewh.commands.noaliasCommands.playtimecore.PlayTimeManagement;
import mp.dottiewh.utils.U;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        PlayTimeManagement.onLeaveManagement(event);
        UUID uuid = event.getPlayer().getUniqueId();
        U.stopForceBlackScreen(uuid); // to be sure
        CinematicsConfig.checkAndStop(uuid);
        CinematicsConfig.stopReproducing(uuid);
    }
}
