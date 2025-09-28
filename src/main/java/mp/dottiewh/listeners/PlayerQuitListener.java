package mp.dottiewh.listeners;

import mp.dottiewh.noaliasCommands.playtimecore.PlayTimeManagement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        PlayTimeManagement.onLeaveManagement(event);
    }
}
