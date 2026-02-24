package mp.dottiewh.listeners.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import mp.dottiewh.commands.aliasCommands.AdminChat;
import mp.dottiewh.config.Config;
import mp.dottiewh.utils.U;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        itemDisplay(event);

        AdminChat.acCore(event);

        //
    }

    public void itemDisplay(AsyncChatEvent event){
        if(event.isCancelled()) return;
        if(!Config.getBoolean("item_display", true)) return;
        Component message = event.message();
        String rawMessage = U.componentToStringMsgRaw(message);
        if(!rawMessage.contains("<item>")) return;


        Player player = event.getPlayer();
        ItemStack main = player.getInventory().getItemInMainHand();
        if(main.isEmpty()) return;

        Component component = Component.text().append(main.displayName()).hoverEvent(main.asHoverEvent()).asComponent();
        Component toGive = message.replaceText(builder-> builder.matchLiteral("<item>").replacement(component));

        event.message(toGive);
    }
}
