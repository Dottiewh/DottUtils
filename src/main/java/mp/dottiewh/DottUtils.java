package mp.dottiewh;

import io.papermc.paper.event.player.AsyncChatEvent;
import mp.dottiewh.items.ItemConfig;
import mp.dottiewh.utils.U;
import mp.dottiewh.aliasCommands.AdminChat;
import mp.dottiewh.aliasCommands.Whitelist;
import mp.dottiewh.config.Config;
import mp.dottiewh.config.CustomConfig;
import mp.dottiewh.noaliasCommands.backcore.BackUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

import java.util.*;

import java.io.File;


public class DottUtils extends JavaPlugin implements Listener {
    private static DottUtils instance;
    private File adminFile;
    //private FileConfiguration adminConfig;
    //private BukkitTask repetitivo;

    public static String prefix = "&5&l[&9&lDott&6&lUtils&5&l] ";
    private final String version = getDescription().getVersion();

    private final Set<String> comandosRegistrados = getDescription().getCommands().keySet();

    private static CustomConfig ymlLists;
    public static CustomConfig ymlConfig;
    public static CustomConfig ymlMessages;
    public static CustomConfig ymlItems;
    public static CustomConfig ymlBackList;
    public static boolean discordCase;

    public void onEnable(){
        instance = this;

        Bukkit.getConsoleSender().sendMessage(
                ChatColor.translateAlternateColorCodes('&',prefix+"&a&lHa sido activado. &c["+version+"]")
        );
        getServer().getPluginManager().registerEvents(this, this);

        initCustomConfig();
        //regEvents(this);
        checkVersion();
        checkSoftDependencys(this);

        U.showAllStatus();
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
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (DottUtils.ymlConfig.getConfig().getBoolean("back_active")){ //Si está en true, sigue, si no no
            BackUtils.movementManagement(event, player);
        }

    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){

        if (DottUtils.ymlConfig.getConfig().getBoolean("back_active")){ //Si está en true, sigue, si no no
            BackUtils.backOnDeathManagement(event);
        }
    }
    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent event){
        U.noPvP(event);
    }
    @EventHandler
    public void onFallDamage(EntityDamageEvent event){
        U.noFall(event); //checkea /du nf
        U.noFall_core(event); // solo sirve para cosas del tipo /jump
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
    public static CustomConfig getRegisteredConfigLists(){
        if (ymlLists == null) {
            U.mensajeConsola("&eConfig aún no cargada...");
        }
        return ymlLists;
    }
    public static CustomConfig getRegisteredConfig(){
        if (ymlConfig == null) {
            U.mensajeConsola("&eConfig aún no cargada...");
        }
        return ymlConfig;
    }
    public static CustomConfig getRegisteredItemConfig(){
        if (ymlConfig == null) {
            U.mensajeConsola("&eConfig aún no cargada...");
        }
        return ymlItems;
    }
    public static CustomConfig getRegisteredMsgConfig(){
        if (ymlConfig == null) {
            U.mensajeConsola("&eConfig aún no cargada...");
        }
        return ymlMessages;
    }
    public static void initCustomConfig(){
        DottUtils plugin = getInstance();

        ymlLists = new CustomConfig("lists.yml", null, plugin, false);
        ymlConfig = new CustomConfig("config.yml", null, plugin, false);
        ymlMessages = new CustomConfig("messages.yml", null, plugin, false);
        ymlItems = new CustomConfig("items.yml", null, plugin, false);
        ymlBackList = new CustomConfig("backlist.yml", "util", plugin, false);
        ymlMessages.registerConfig();
        ymlConfig.registerConfig();
        ymlLists.registerConfig();
        ymlItems.registerConfig();
        ymlBackList.registerConfig();
        //
        prefix = ymlMessages.getConfig().getString("prefix");

        Config.configInit();
        ItemConfig.itemConfigInit();
    }
    /*private static void regEvents(JavaPlugin plugin, Set<String> comandosSet){
        for (String cmd : comandosSet){
            PluginCommand pc = plugin.getCommand(cmd);
            if (pc != null) {
                pc.setExecutor(plugin);         // tu onCommand ya maneja todo
                pc.setTabCompleter(plugin);     // si implementas onTabComplete / TabCompleter
            } else {
                plugin.getLogger().warning("Comando '" + cmd + "' no encontrado en plugin.yml");
            }
        }
    }*/
    private void checkSoftDependencys(JavaPlugin plugin){
        if (Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")) {
            U.mensajeConsola("&6&lSe ha detectado al plugin &fDiscordSRV&a&l!");
            discordCase = true;
        } else {
            discordCase = false;
        }
    }

    public static DottUtils getInstance(){
        return instance;
    }
    private void checkVersion(){
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            String ultimaVersion = U.getLastVersionGithub();

            if (ultimaVersion != null && !ultimaVersion.equalsIgnoreCase(version)) {
                Bukkit.getScheduler().runTask(this, () -> {
                    U.mensajeConsola("&e&lEstás usando una versión no actualizada! &cYours: &6"+version+" &8| &clastest: &6"+ultimaVersion);
                    U.mensajeConsola("&e&lDescarga la última version en: &fhttps://github.com/Dottiewh/DottUtils/releases");
                });
            }else{
                Bukkit.getScheduler().runTask(this, () -> {
                    U.mensajeConsola("&aEstás usando la última versión! &6"+ultimaVersion);
                });
            }
        });
    }
}
