package mp.dottiewh.cinematics;

import com.destroystokyo.paper.SkinParts;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import mp.dottiewh.DottUtils;
import mp.dottiewh.cinematics.exceptions.CinematicFileNull;
import mp.dottiewh.config.CustomConfig;
import mp.dottiewh.utils.U;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

public class CinematicsConfig {
    private static final DottUtils instance = DottUtils.getInstance();
    private static final Plugin plugin = DottUtils.getPlugin();
    private static final HashMap<UUID, BukkitRunnable> mapaRunnables = new HashMap<>();
    private static final HashMap<UUID, BukkitRunnable> mapaCountdown = new HashMap<>();
    private static final HashMap<UUID, String> mapaOnGoingRecords = new HashMap<>();

    private static final HashMap<UUID, Map.Entry<GameMode, Mannequin>> mapaPlayerData = new HashMap<>();
    private static final HashMap<UUID, Location> mapaPlayerDataTwo = new HashMap<>();
    private static final HashMap<UUID, List<BukkitRunnable>> mapaPlayerReproduce = new HashMap<>();

    private static final HashMap<UUID, Location> mapaPendingTP = new HashMap<>();

    public static void startRecording(Player p, String fileName, long period){
        UUID uuid = p.getUniqueId();
        if(mapaRunnables.containsKey(uuid)){
            cineMsg("&cYa estás grabando una animación.", p);
            return;
        }
        if(checkFileExists(fileName)){
            cineMsg("&cYa existe una cinematica con ese nombre, borrala en tus archivos.", p);
            return;
        }

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = p.getLocation();
                registerLocation(loc, fileName, period);
                U.staticActionBar(p, "&c&l● &cGrabando...");
            }
        };
        mapaRunnables.put(uuid, runnable);
        mapaOnGoingRecords.put(uuid, fileName);

        cineMsg("&aEmpezaras a grabar la cinemática &f"+fileName+" &aen 5 segundos. &e("+period+")", p);
        U.countdownForTarget(p, plugin, 5, "&6Tiempo restante: ");

        BukkitRunnable countdown = new BukkitRunnable() {
            @Override
            public void run() {
                cineMsg("&eEstás grabando! Puedes usar &6/du cinematic record stop&e, para parar la grabación.", p);
                runnable.runTaskTimer(plugin, 0, period);
                removeCountdownMap(uuid);
            }
        };
        countdown.runTaskLater(plugin, 5*20L);
        mapaCountdown.put(uuid, countdown);

        CustomConfig config = getFile(fileName, true);
        if(config==null){
            throw new CinematicFileNull("cinematics/"+fileName+".yml", "No existe el yml de la canción. No se guarda intervalo.");
        }
        config.getConfig().set("Period", period);
        config.saveConfig();
        List<String> stringList = new ArrayList<>();
        config.getConfig().set("Locations", stringList);
        config.saveConfig();
    }


    private static void registerLocation(Location loc, String fileName, long period){
        CustomConfig config = getFile(fileName, false);
        if(config==null){
            throw new CinematicFileNull("cinematics/"+fileName+".yml", "No existe el yml de la canción. No se puede registrar loc");
        }
        List<String> listaLocations = config.getConfig().getStringList("Locations");

        double x=loc.getX(), y=loc.getY(), z=loc.getZ();
        float yaw=loc.getYaw(), pitch=loc.getPitch();
        String sX=String.valueOf(x), sY=String.valueOf(y), sZ=String.valueOf(z);
        String sYaw=String.valueOf(yaw), sPitch=String.valueOf(pitch);
        String sWorld=loc.getWorld().getName();

        String sPosition = String.join(";", sWorld, sX, sY, sZ, sYaw, sPitch);

        listaLocations.add(sPosition);
        config.getConfig().set("Locations", listaLocations);
        config.saveConfig();
    }
    //
    public static void stopRegister(Player p){
        UUID uuid = p.getUniqueId();
        checkAndStop(uuid);
        cineMsg("&aSe ha mandado la orden de dejar de registrar cinemáticas.", p);
    }
    //
    public static void checkAndStop(UUID uuid){
        U.stopStaticActionBar(uuid);
        removeCountdownMap(uuid);
        if(!(mapaRunnables.containsKey(uuid))) return;

        mapaRunnables.get(uuid).cancel();
        mapaRunnables.remove(uuid);
        // destacar END en record
        if(!(mapaOnGoingRecords.containsKey(uuid))) return;
        String fileName = mapaOnGoingRecords.remove(uuid);

        CustomConfig file = getFile(fileName, false);
        if(file==null) return;
        List<String> sList = file.getConfig().getStringList("Locations");
        sList.add("end");
        file.getConfig().set("Locations", sList);
        file.saveConfig();
    }
    private static void removeCountdownMap(UUID uuid){
        if(mapaCountdown.containsKey(uuid)){
            try{
                mapaCountdown.get(uuid).cancel();
            } catch (Exception e) {
                U.mensajeConsolaNP("&c"+ Arrays.toString(e.getStackTrace()));
            }
            mapaCountdown.remove(uuid);
        }
    }

    @NotNull
    private static CustomConfig createNewFile(String name){
        File archivo = new File(DottUtils.folderCinematic, name+".yml");
        try{
            if(!archivo.exists()){
                archivo.createNewFile();
            }
        } catch (Exception e) {
            U.mensajeConsolaNP("&c"+Arrays.toString(e.getStackTrace()));
        }

        CustomConfig config  = new CustomConfig(name+".yml", "cinematics", instance, false);

        if(!(archivo.exists())){
            throw new CinematicFileNull("cinematics/"+name+".yml", "Al intentar conseguir el config da null. Se creo mal el archivo?");
        }
        config.registerConfig();
        return config;
    }

    @Nullable
    private static CustomConfig getFile(String name, boolean forceCreate){
        CustomConfig config = new CustomConfig(name+".yml", "cinematics", instance, false);

        File archivo = new File(DottUtils.folderCinematic, name+".yml");
        boolean success = archivo.exists();
        if(!success){
            if(forceCreate){
                return createNewFile(name);
            }else{return null;}
        }
        config.registerConfig();
        return config;
    }
    private static boolean checkFileExists(String name){
        CustomConfig config = getFile(name, false);
        return !(config==null);
    }

    //------------REPRODUCE THINSG---------------------
    public static boolean reproduceCinematic(Player p, String fileName, boolean clonePlayer) {
        CustomConfig config = getFile(fileName, false);
        if (!(checkFileExists(fileName)) || config == null) return false;

        UUID uuid = p.getUniqueId();
        stopReproducing(uuid);
        GameMode gmToDeliver = p.getGameMode();
        Mannequin npcToDeliver = null;

        if (clonePlayer&&!(p.getGameMode().equals(GameMode.SPECTATOR))){
            Location pLoc = p.getLocation();
            Mannequin npc = (Mannequin) pLoc.getWorld().spawnEntity(pLoc, EntityType.MANNEQUIN);

            //sobre texturas
            ResolvableProfile.Builder rpBuilder = ResolvableProfile.resolvableProfile();

            ResolvableProfile.SkinPatchBuilder spBuilder = ResolvableProfile.SkinPatch.skinPatch();
            PlayerTextures playerTextures = p.getPlayerProfile().getTextures();
            spBuilder.model(playerTextures.getSkinModel());

            rpBuilder.skinPatch(spBuilder.build());
            rpBuilder.name(p.getName());

            //mainhand
            npc.setProfile(rpBuilder.build());
            npc.setMainHand(p.getMainHand());
            //skinparts
            npc.setSkinParts(SkinParts.allParts());
            //other
            Component cName = U.componentColor(p.getName());
            npc.setDescription(Component.empty());
            npc.customName(cName);
            npc.setCustomNameVisible(true);
            npc.setGravity(false);

            npcToDeliver = npc;
        }
        Map.Entry<GameMode, Mannequin> entryToDeliver = new AbstractMap.SimpleEntry<>(gmToDeliver, npcToDeliver);

        mapaPlayerData.put(uuid, entryToDeliver);
        Location playerLoc = p.getLocation();
        mapaPlayerDataTwo.put(uuid, playerLoc);

        U.hidePlayerForAll(p);
        p.setGameMode(GameMode.SPECTATOR);

        long period = config.getConfig().getLong("Period");
        int count = 0;
        List<BukkitRunnable> lRunnables = new ArrayList<>();

        for(String input : config.getConfig().getStringList("Locations")){
            if(input.equalsIgnoreCase("end")){
                BukkitRunnable finalTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        stopReproducing(uuid);
                    }
                };
                finalTask.runTaskLater(plugin, count*period);
                lRunnables.add(finalTask);
                break;
            }
            //
            String[] aI = input.split(";"); // stands for arrayInput

            World world = Bukkit.getWorld(aI[0]);
            double x = Double.parseDouble(aI[1]), y = Double.parseDouble(aI[2]), z = Double.parseDouble(aI[3]);
            float yaw = Float.parseFloat(aI[4]), pitch = Float.parseFloat(aI[5]);

            Location loc = new Location(world, x, y, z, yaw, pitch);


            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    p.teleportAsync(loc);
                }
            };
            task.runTaskLater(plugin, count*period);
            lRunnables.add(task);

            count++;
        }

        mapaPlayerReproduce.put(uuid, lRunnables);
        return true;
    }
    public static void stopReproducing(UUID uuid){
        if(!(mapaPlayerData.containsKey(uuid))) return;

        Player player = Bukkit.getPlayer(uuid);
        boolean online = !(player==null);

        //DATA
        Map.Entry<GameMode, Mannequin> eData = mapaPlayerData.remove(uuid);
        if(online) player.setGameMode(eData.getKey());

        Mannequin npc = eData.getValue();
        npc.remove();
        //DATA 2
        Location oldLoc = mapaPlayerDataTwo.remove(uuid);

        if(online)player.teleport(oldLoc);
        else mapaPendingTP.put(uuid, oldLoc);

        // STOP TASK AND REMOVE
        List<BukkitRunnable> lRunnables = mapaPlayerReproduce.remove(uuid);
        Iterator<BukkitRunnable> it = lRunnables.iterator();
        while(it.hasNext()){
            BukkitRunnable task = it.next();
            task.cancel();
            it.remove();
        }
        // misc
        U.unHidePlayerForAll(player);
    }
    //
    public static void onJoinCheck(Player player){
        UUID uuid = player.getUniqueId();
        if(!(mapaPendingTP.containsKey(uuid))) return;
        Location locToTp = mapaPendingTP.remove(uuid);
        player.teleport(locToTp);
    }
    //
    public static void cineMsg(String msg, Player p){
        String prefix = U.getMsgPath("cinematic_prefix", "&6&l[&e&lCinematics&6&l] ");
        U.targetMessageNP(p, prefix+msg);
    }
}
