package mp.dottiewh.commands.noaliasCommands;

import mp.dottiewh.commands.Commands;
import mp.dottiewh.config.Config;
import mp.dottiewh.config.CustomConfig;
import mp.dottiewh.utils.U;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class CustomSpawn extends Commands {
    public static CustomConfig config = null;
    private static boolean forceTp = false;
    private final static HashMap<String, String> playerData = new HashMap<>(); // playerName, locationName

    //
    public static void onReload(){
        reloadPlayerData();
        forceTp= Config.getBoolean("custom_spawn_forcetp", false);
    }

    public static void reloadPlayerData(){
        if(config==null) return;
        ConfigurationSection playerSection = config.getConfig().getConfigurationSection("Players");
        if(playerSection==null) return;

        playerData.clear();
        for(String locId : playerSection.getKeys(false)){
            List<String> playerList = playerSection.getStringList(locId);
            U.mensajeDebugConsole(locId+" | "+playerList);
            playerList.forEach(p-> playerData.put(p, locId));
        }
    }

    public static void onReSpawn(PlayerRespawnEvent event){
        if(config==null) return;
        Player player = event.getPlayer();
        String name = player.getName();

        if(!playerData.containsKey(name)) return;

        Location respawnLoc = player.getRespawnLocation(false);
        U.mensajeDebugConsole("respawnLoc");

        if((!forceTp)&&respawnLoc==null) return;

        Location toTeleportLoc = getLocation(playerData.get(name));
        if(toTeleportLoc==null) return;

        player.teleportAsync(toTeleportLoc);
        //player.setRespawnLocation(toTeleportLoc, true); // !!!!!!!!!!!!
    }
    @Nullable
    public static Location getLocation(String locId){
        if(config==null) return null;
        U.mensajeDebugConsole("get location");

        ConfigurationSection locSection = config.getConfig().getConfigurationSection("Locations");
        if(locSection==null) return null;

        U.mensajeDebugConsole("getting output");
        String output = locSection.getString(locId, null);
        if(output==null) return null;

        String[] outputArray = output.split(";");

        World world = Bukkit.getWorld(outputArray[0]);
        double x = Double.parseDouble(outputArray[1]);
        double y = Double.parseDouble(outputArray[2]);
        double z = Double.parseDouble(outputArray[3]);
        float yaw = 0f, pitch = 0f;

        if(outputArray.length>4){
            yaw = Float.parseFloat(outputArray[4]);
            pitch = Float.parseFloat(outputArray[5]);
        }
        return new Location(world, x, y, z, yaw, pitch);
    }
}
