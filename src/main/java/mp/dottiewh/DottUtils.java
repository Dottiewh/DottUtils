package mp.dottiewh;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

import java.util.*;

import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import java.io.IOException;

import org.bukkit.scheduler.BukkitTask;


public class DottUtils extends JavaPlugin implements Listener{
    private File adminFile;
    private FileConfiguration adminConfig;
    private BukkitTask repetitivo;

    public static String prefix = "&5&l[&9&lDott&6&lUtils&5&l] ";
    private final String version = getDescription().getVersion();

    private Set<String> comandosRegistrados = getDescription().getCommands().keySet();



    public void onEnable(){
        Bukkit.getConsoleSender().sendMessage(
                ChatColor.translateAlternateColorCodes('&',prefix+"&a&lHa sido activado. &c["+version+"]")
        );
        getServer().getPluginManager().registerEvents(this, this);
    }
    public void onDisable(){
        Bukkit.getConsoleSender().sendMessage(
                ChatColor.translateAlternateColorCodes('&',prefix+"&c&lHa sido desactivado. &c["+version+"]")
        );
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
}
