package mp.dottiewh.listeners;

import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.*;
import github.scarsz.discordsrv.util.DiscordUtil;
import mp.dottiewh.commands.aliasCommands.AdminChat;
import org.bukkit.plugin.Plugin;

// Para ver ejemplos revisar https://github.com/DiscordSRV/DiscordSRV-ApiTest/blob/master/src/main/java/com/discordsrv/apitest/DiscordSRVListener.java
public class DiscordSRVListener {
    private final Plugin plugin;

    public DiscordSRVListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        // Example of using JDA's events
        // We need to wait until DiscordSRV has initialized JDA, thus we're doing this inside DiscordReadyEvent
        DiscordUtil.getJda().addEventListener(new JDAListener(plugin));

        // ... we can also do anything other than listen for events with JDA now,
        AdminChat.sendMsgToAdminChatDS("", "**-------:green_circle: Admin Chat funcionando-------**", false);
        // see https://ci.dv8tion.net/job/JDA/javadoc/ for JDA's javadoc
        // see https://github.com/DV8FromTheWorld/JDA/wiki for JDA's wiki
    }

    @Subscribe(priority = ListenerPriority.MONITOR)
    public void discordMessageReceived(DiscordGuildMessageReceivedEvent event) {
        //MENSAJE DEL DISCORD
        AdminChat.discordChatCoreFromDiscord(event);
    }
}
