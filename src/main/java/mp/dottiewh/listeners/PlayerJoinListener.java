package mp.dottiewh.listeners;

import mp.dottiewh.noaliasCommands.playtimecore.PlayTimeManagement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        PlayTimeManagement.onJoinManagement(event);
    }
}
