package mp.dottiewh;

import github.scarsz.discordsrv.DiscordSRV;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import mp.dottiewh.cinematics.CinematicsConfig;
import mp.dottiewh.items.ItemConfig;
import mp.dottiewh.music.MusicConfig;
import mp.dottiewh.commands.noaliasCommands.playtimecore.PlayTimeManagement;
import mp.dottiewh.commands.Commands;
import mp.dottiewh.utils.U;
import mp.dottiewh.config.Config;
import mp.dottiewh.config.CustomConfig;
import mp.dottiewh.listeners.*;
import org.bukkit.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

import java.util.*;

import java.io.File;


public class DottUtils extends JavaPlugin implements Listener {
    private static DottUtils instance;
    private static Plugin plugin;
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
    public static CustomConfig ymlPlayTime;
    public static CustomConfig ymlMusic;
    public static CustomConfig ymlInternalItems;
    public static File folderCinematic;
    public static File folderMusic;

    public static boolean discordCase;
    private final DiscordSRVListener discordsrvListener = new DiscordSRVListener(this);

    public void onEnable(){
        instance = this;
        plugin = this;

        Bukkit.getConsoleSender().sendMessage(
                ChatColor.translateAlternateColorCodes('&',prefix+"&a&lHa sido activado. &c["+version+"]")
        );

        initCustomConfig();
        checkSoftDependencys();
        regEvents();
        registrarComandos();
        checkVersion();

        //Other things
        PlayTimeManagement.onEnableManagement(); // para cosas del playtime
        U.showAllStatus();
    }
    public void onDisable(){
        PlayTimeManagement.onDisableManagement();
        CinematicsConfig.onDisableCheck();
        U.cleanOnDisable();

        if (ymlConfig != null) {
            ymlConfig.saveConfig();
        }
        if (discordCase){
            DiscordSRV.api.unsubscribe(discordsrvListener);
        }

        Bukkit.getConsoleSender().sendMessage(
                ChatColor.translateAlternateColorCodes('&',prefix+"&c&lHa sido desactivado. &c["+version+"]")
        );
        instance = null;
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
        DottUtils instance = getInstance();

        ymlLists = new CustomConfig("lists.yml", null, instance, false);
        ymlConfig = new CustomConfig("config.yml", null, instance, false);
        ymlMessages = new CustomConfig("messages.yml", null, instance, false);
        ymlItems = new CustomConfig("items.yml", null, instance, false);
        ymlBackList = new CustomConfig("backlist.yml", "util", instance, false);
        ymlPlayTime = new CustomConfig("playtimes.yml", "util", instance, false);
        ymlInternalItems = new CustomConfig("internalitems.yml", "util", instance, false);

        ymlMessages.registerConfig();
        ymlConfig.registerConfig();
        ymlLists.registerConfig();
        ymlItems.registerConfig();
        ymlBackList.registerConfig();
        ymlPlayTime.registerConfig();
        ymlInternalItems.registerConfig();
        //
        prefix = ymlMessages.getConfig().getString("prefix");

        Config.configInit();
        ItemConfig.itemConfigInit();


        //cinematics related
        folderCinematic = new File(instance.getDataFolder(), "cinematics");
        if(!folderCinematic.exists()){
            folderCinematic.mkdirs();
        }

        //music related
        folderMusic = new File(instance.getDataFolder(), "musics");
        if(!folderMusic.exists()){
            folderMusic.mkdirs();
        }
        MusicConfig.initMusicConfig();
    }
    private void regEvents(){
        regFormat(new ChatListener());
        regFormat(new EntityAttackListener());
        regFormat(new FallDamageListener());
        regFormat(new PlayerDeathListener());
        regFormat(new PlayerMoveListener());
        regFormat(new PlayerPreLoginListener());
        regFormat(new ServerCommandListener());
        regFormat(new PlayerJoinListener());
        regFormat(new PlayerQuitListener());
        regFormat(new PlayerInteractListener());
        regFormat(new PlayerInventoryItemClickListener());
        regFormat(new PlayerItemDropListener());

        if (discordCase){
            DiscordSRV.api.subscribe(discordsrvListener);
        }
    }
    private void regFormat(Listener listener){
        getServer().getPluginManager().registerEvents(listener, this);
    }
    private void checkSoftDependencys(){
        if (Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")) {
            U.mensajeConsola("&6&lSe ha detectado al plugin &fDiscordSRV&a&l!");
            discordCase = true;

            String channelID = ymlConfig.getConfig().getString("discord_adminchat_channel");
            if(channelID!=null&&channelID.equalsIgnoreCase("CHANNELID")){
                U.mensajeConsola("&eNo tienes especificado un canal de discord para adminchat en la config!");
            }
        } else {
            discordCase = false;
        }
    }

    public static DottUtils getInstance(){
        return instance;
    }
    public static Plugin getPlugin(){
        return plugin;
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
    private void registrarComandos(){
        // reg alias
        this.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS,
                event->{
                    event.registrar().register(
                        Commands.createAlias(this, "du").build(),
                            "main command alias"
                    );
                    event.registrar().register(
                            Commands.createAlias(this, "dottutils").build(),
                            "main command"
                    );
                }
        );
        Commands.regNoAliasCommands(this);
    }
}
