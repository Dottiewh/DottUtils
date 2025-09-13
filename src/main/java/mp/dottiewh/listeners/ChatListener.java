package mp.dottiewh.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import mp.dottiewh.aliasCommands.AdminChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event){

        AdminChat.acCore(event);
    }
}
