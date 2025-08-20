package mp.dottiewh;

import io.papermc.paper.event.player.AsyncChatEvent;
import mp.dottiewh.aliasCommands.AdminChat;
import mp.dottiewh.aliasCommands.Whitelist;
import mp.dottiewh.config.Config;
import mp.dottiewh.config.CustomConfig;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

import java.util.*;

import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;

import org.bukkit.scheduler.BukkitTask;


public class DottUtils extends JavaPlugin implements Listener {
    private static DottUtils instance;
    private File adminFile;
    private FileConfiguration adminConfig;
    private BukkitTask repetitivo;

    public static String prefix = "&5&l[&9&lDott&6&lUtils&5&l] ";
    private final String version = getDescription().getVersion();

    private Set<String> comandosRegistrados = getDescription().getCommands().keySet();

    private static CustomConfig ymlConfig;

    public void onEnable(){
        instance = this;

        Bukkit.getConsoleSender().sendMessage(
                ChatColor.translateAlternateColorCodes('&',prefix+"&a&lHa sido activado. &c["+version+"]")
        );
        getServer().getPluginManager().registerEvents(this, this);

        initCustomConfig();

        U.mensajeConsola("&9&lWhitelist: &e&l"+Config.getWhiteListStatus());
    }
    public void onDisable(){
        Bukkit.getConsoleSender().sendMessage(
                ChatColor.translateAlternateColorCodes('&',prefix+"&c&lHa sido desactivado. &c["+version+"]")
        );


        if (ymlConfig != null) {
            ymlConfig.saveConfig();
        }
        instance = null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        //new Commands(comandosRegistrados, sender, cmd, label, args);
        Commands.commandCore(comandosRegistrados, sender, cmd, label, args);

        return true;
    }


    @EventHandler
    public void onFallDamage(EntityDamageEvent event){
        U.noFall_core(event);
    }

    @EventHandler
    public void onChat(AsyncChatEvent event){

        AdminChat.acCore(event);
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        AdminChat.consoleChatCore(event);
    }
    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        Whitelist.checkWhitelist(event);
    }
    //----------
    public static CustomConfig getRegisteredConfig(){
        if (ymlConfig == null) {
            U.mensajeConsola("&eConfig aún no cargada...");
        }
        return ymlConfig;
    }
    public static void initCustomConfig(){
        DottUtils plugin = getInstance();

        ymlConfig = new CustomConfig("lists.yml", null, plugin, false);
        ymlConfig.registerConfig();
        Config.configInit();
    }
    public static DottUtils getInstance(){
        return instance;
    }
}
