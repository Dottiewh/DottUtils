package mp.dottiewh.listeners.player;

import mp.dottiewh.commands.noaliasCommands.CustomSpawn;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        CustomSpawn.onReSpawn(event);
    }
}
