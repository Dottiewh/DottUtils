package mp.dottiewh;

import mp.dottiewh.config.CustomConfig;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

import java.util.*;

import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import java.io.IOException;

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

    //----------
    public static CustomConfig getRegisteredConfig(){
        if (ymlConfig == null) {
            U.mensajeConsola("&eConfig aún no cargada...");
        }
        return ymlConfig;
    }
    public static void initCustomConfig(){
        DottUtils plugin = getInstance();

        ymlConfig = new CustomConfig("adminlist.yml", null, plugin, false);
        ymlConfig.registerConfig();
        U.configInit();
    }
    public static DottUtils getInstance(){
        return instance;
    }
}
