package mp.dottiewh.noaliasCommands.backcore;

import mp.dottiewh.DottUtils;
import mp.dottiewh.Utils.U;
import mp.dottiewh.config.Config;
import mp.dottiewh.config.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class BackUtils {

    public static void backOnDeathManagement(PlayerDeathEvent event){
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        String name = player.getName();

        Location deathLoc = player.getLocation();
        double x = deathLoc.getX();
        double y = deathLoc.getY();
        double z = deathLoc.getZ();
        World world = deathLoc.getWorld();

        addDeathLoc(name, x, y, z ,world);
    }
    public static void addDeathLoc(String name, double x, double y, double z, World world){
        ConfigurationSection sectionM = getMainSection();
        if (sectionM==null){
            onErrorChance();
            return;
        }
        ConfigurationSection section = sectionM.createSection(name);
        section.set("x", format(x));
        section.set("y", format(y));
        section.set("z", format(z));
        section.set("world", world.getName());

        getBackList().saveConfig();
    }
    public static Location getDeathLoc(String name){
        ConfigurationSection sectionM = getMainSection();
        if (sectionM==null){
            onErrorChance();
            return null;
        }
        ConfigurationSection section = sectionM.getConfigurationSection(name);
        if (section==null) return null;

        String worldS = section.getString("world"); if (worldS==null) return null;
        World world = Bukkit.getWorld(worldS); if(world == null) return null;
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");

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
        U.STmensajeConsola("&cRevisa tu util>backlist.yml | Es posible que esté vacío o corrupto.");
        U.STmensajeConsolaNP("&cIntentando regenerar...");
        getBackList().getConfig().createSection("Deaths");
        getBackList().saveConfig();
    }
    private static double format(double num){
        return U.truncar(num, 3);
    }
}
