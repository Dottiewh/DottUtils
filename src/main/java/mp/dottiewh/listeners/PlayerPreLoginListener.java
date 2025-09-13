package mp.dottiewh.listeners;

import mp.dottiewh.aliasCommands.Whitelist;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PlayerPreLoginListener implements Listener {

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        Whitelist.checkWhitelist(event);
    }
}
