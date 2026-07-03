package mp.dottiewh.listeners.player;

import mp.dottiewh.commands.aliasCommands.Maintenance;
import mp.dottiewh.commands.aliasCommands.Whitelist;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PlayerPreLoginListener implements Listener {

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        Maintenance.checkMaintenance(event);
        Whitelist.checkWhitelist(event);
    }
}
