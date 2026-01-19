package mp.dottiewh.listeners;

import mp.dottiewh.commands.aliasCommands.AdminChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

public class ServerCommandListener implements Listener {

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        AdminChat.consoleChatCore(event);
    }
}
