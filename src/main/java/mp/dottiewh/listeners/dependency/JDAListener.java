package mp.dottiewh.listeners.dependency;

import github.scarsz.discordsrv.dependencies.jda.api.events.guild.GuildUnavailableEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import mp.dottiewh.utils.U;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

//DISCORD API LISTENER
public class JDAListener extends ListenerAdapter {
    private final Plugin plugin;

    public JDAListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override // we can use any of JDA's events through ListenerAdapter, just by overriding the methods
    public void onGuildUnavailable(@NotNull GuildUnavailableEvent event) {
        U.mensajeConsola("&c&l-El servidor se ha vuelto INACCESIBLE!-");
    }
}
