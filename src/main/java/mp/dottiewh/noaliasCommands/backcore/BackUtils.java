package mp.dottiewh.noaliasCommands.backcore;

import mp.dottiewh.DottUtils;
import mp.dottiewh.Utils.Crypto;
import mp.dottiewh.Utils.U;
import mp.dottiewh.config.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class BackUtils {

    public static void backOnDeathManagement(PlayerDeathEvent event){
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        String name = player.getName();
        UUID uuid = player.getUniqueId();

        Location deathLoc = player.getLocation();
        double x = deathLoc.getX();
        double y = deathLoc.getY();
        double z = deathLoc.getZ();
        World world = deathLoc.getWorld();

        addDeathLoc(name, x, y, z ,world, uuid);
    }
    public static void addDeathLoc(String name, double x, double y, double z, World world, UUID uuid){
        ConfigurationSection sectionM = getMainSection();
        if (sectionM==null){
            onErrorChance();
            return;
        }
        ConfigurationSection section = sectionM.createSection(name);
        if(DottUtils.ymlConfig.getConfig().getInt("back_encrypt_mode")==0){
            section.set("x", format(x));
            section.set("y", format(y));
            section.set("z", format(z));
        }else{
            section.set("x", keyFormat(x, uuid));
            section.set("y", keyFormat(y, uuid));
            section.set("z", keyFormat(z, uuid));
        }

        section.set("world", world.getName());

        //encrypt mode
        if(DottUtils.ymlConfig.getConfig().getBoolean("add_encrypt_mode_to_save")){
            section.set("encrypt_mode", DottUtils.ymlConfig.getConfig().getInt("back_encrypt_mode"));
        }

        getBackList().saveConfig();
    }
    public static Location getDeathLoc(String name, UUID uuid){
        double x=0, y=0, z=0;

        ConfigurationSection sectionM = getMainSection();
        if (sectionM==null){
            onErrorChance();
            return null;
        }
        ConfigurationSection section = sectionM.getConfigurationSection(name);
        if (section==null) return null;

        String worldS = section.getString("world"); if (worldS==null) return null;
        World world = Bukkit.getWorld(worldS); if(world == null) return null;

        int customMode = section.getInt("encrypt_mode", -1);
        if(DottUtils.ymlConfig.getConfig().getInt("back_encrypt_mode")==0 && customMode==-1) {
            x = section.getDouble("x");
            y = section.getDouble("y");
            z = section.getDouble("z");
        }else{
            if (customMode==-1){
                x = Crypto.decodeForBack(section.getString("x"), uuid);
                y = Crypto.decodeForBack(section.getString("y"), uuid);
                z = Crypto.decodeForBack(section.getString("z"), uuid);
            }else{
                x = Crypto.decodeForBack(section.getString("x"), uuid, customMode);
                y = Crypto.decodeForBack(section.getString("y"), uuid, customMode);
                z = Crypto.decodeForBack(section.getString("z"), uuid, customMode);
            }
        }

        return new Location(world, x, y, z);
    }
    public static void delDeathLoc(String name){
        ConfigurationSection sectionM = getMainSection();
        sectionM.set(name, null);

        getBackList().saveConfig();
    }
    public static void movementManagement(PlayerMoveEvent event, Player player){
        if (event.isCancelled()) return;

        double x_from, z_from, x_to, z_to;
        x_from = event.getFrom().getBlockX();
        z_from = event.getFrom().getBlockZ();

        x_to = event.getTo().getBlockX();
        z_to = event.getTo().getBlockZ();

        if (x_from!=x_to||z_from!=z_to){
           if (BackCommand.checkAndCancelTask(player)){
               U.targetMessage(player, "&cTe has movido, así que se ha cancelado tu &6back&c!");
           }
        }
    }

    //---------------
    private static ConfigurationSection getMainSection(){
        return DottUtils.ymlBackList.getConfig().getConfigurationSection("Deaths");
    }
    private static CustomConfig getBackList(){
        return DottUtils.ymlBackList;
    }
    private static CustomConfig getConfig(){
        return DottUtils.ymlConfig;
    }
    private static void onErrorChance(){
        U.mensajeConsola("&cRevisa tu util>backlist.yml | Es posible que esté vacío o corrupto.");
        U.mensajeConsolaNP("&cIntentando regenerar...");
        getBackList().getConfig().createSection("Deaths");
        getBackList().saveConfig();
    }
    private static double format(double num){
        return U.truncar(num, 3);
    }
    private static String keyFormat(double num, UUID uuid){
        double value = U.truncar(num, 3);
        return Crypto.encodeForBack(value, uuid);
    }
    //--
    public static void backSendMsg(String msg, Player player){
        String prefix = U.getMsgPath("back_prefix");
        U.targetMessageNP(player, prefix+msg);
    }
    public static void backSendMsgConsole(String msg){
        String prefix = U.getMsgPath("back_prefix");
        U.mensajeConsolaNP(prefix+msg);
    }
    public static void backSendMsgSender(String msg,CommandSender sender){
        String prefix = U.getMsgPath("back_prefix");
        sender.sendMessage(U.mensajeConColor(prefix+msg));
    }
}
