package mp.dottiewh.cinematics;

import com.destroystokyo.paper.SkinParts;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import mp.dottiewh.DottUtils;
import mp.dottiewh.cinematics.exceptions.CinematicFileDontExist;
import mp.dottiewh.cinematics.exceptions.CinematicFileNull;
import mp.dottiewh.cinematics.exceptions.CinematicRecordingHasNotStarted;
import mp.dottiewh.config.CustomConfig;
import mp.dottiewh.utils.U;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

// TO DO, cinematics that are in a chunk not loaded, are going to be weird
public class CinematicsConfig {
    private static final DottUtils instance = DottUtils.getInstance();
    private static final Plugin plugin = DottUtils.getPlugin();
    private static final HashMap<UUID, BukkitRunnable> mapaRunnables = new HashMap<>();
    private static final HashMap<UUID, BukkitRunnable> mapaCountdown = new HashMap<>();
    private static final HashMap<UUID, Map.Entry<String, Long>> mapaOnGoingRecords = new HashMap<>();

    private static final HashMap<UUID, Map.Entry<GameMode, Location>> mapaPlayerData = new HashMap<>();
    private static final HashMap<UUID, Map.Entry<Mannequin, TextDisplay>> mapaPlayerDataTwo = new HashMap<>();
    private static final HashMap<UUID, List<BukkitRunnable>> mapaPlayerReproduce = new HashMap<>();

    private static final HashMap<UUID, Map.Entry<GameMode, Location>> mapaPending = new HashMap<>();

    private static BukkitRunnable getMainRun(Player p, String fileName, long period){
        return new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = p.getEyeLocation();
                registerLocation(loc, fileName, period);
            }
        };
    }
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

        BukkitRunnable runnable = getMainRun(p, fileName, period);
        mapaRunnables.put(uuid, runnable);

        Map.Entry<String, Long> entryRecordToDeliver = new AbstractMap.SimpleEntry<>(fileName, period);
        mapaOnGoingRecords.put(uuid, entryRecordToDeliver);

        cineMsg("&aEmpezaras a grabar la cinemática &f"+fileName+" &aen 5 segundos. &e("+period+")", p);
        U.countdownForTarget(p, plugin, 5, "&6Tiempo restante: ");
        U.playsoundTarget(p, Sound.BLOCK_NOTE_BLOCK_BASS,10,1);

        BukkitRunnable countdown = new BukkitRunnable() {
            @Override
            public void run() {
                cineMsg("&eEstás grabando! Puedes usar &6/du cinematic record stop&e, para parar la grabación.", p);
                U.playsoundTarget(p, Sound.BLOCK_NOTE_BLOCK_BELL,10,1);
                runnable.runTaskTimer(plugin, 0, period);
                U.staticActionBar(p, "&c&l● &cGrabando...");
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

    public static void pauseRecord(Player p) throws CinematicRecordingHasNotStarted{
        UUID uuid = p.getUniqueId();
        if(!(mapaRunnables.containsKey(uuid))) throw new CinematicRecordingHasNotStarted(p.getName(), "El usuario quiere pausar una animación no registrada.");
        BukkitRunnable mainRun = mapaRunnables.remove(uuid);
        String fileName = mapaOnGoingRecords.get(uuid).getKey();
        long period = mapaOnGoingRecords.get(uuid).getValue();

        if(mainRun!=null){
            try {
                mainRun.cancel();
                mapaRunnables.put(uuid, getMainRun(p, fileName, period));
                U.staticActionBar(p, "&6&l● &eEn pausa...");
            } catch (IllegalStateException e){
                throw new CinematicRecordingHasNotStarted(p.getName(), "El usuario quiere pausar una animación que no ha comenzado");
            }
        }
    }
    public static void resumeRecord(Player p) throws CinematicRecordingHasNotStarted{
        UUID uuid = p.getUniqueId();
        if(!(mapaRunnables.containsKey(uuid))) throw new CinematicRecordingHasNotStarted(p.getName(), "El usuario quiere pausar una animación no registrada.");
        BukkitRunnable mainRun = mapaRunnables.get(uuid);
        if(mainRun!=null){
            try {
                String fileName = mapaOnGoingRecords.get(uuid).getKey();
                Long period = mapaOnGoingRecords.get(uuid).getValue();
                mainRun.runTaskTimer(plugin, 5L, period);
                cineMsg("&aResumiste correctamente la grabación de "+fileName, p);
                U.staticActionBar(p, "&c&l● &cGrabando...");
            } catch (IllegalStateException e){
                throw new CinematicRecordingHasNotStarted(p.getName(), "El usuario quiere pausar una animación que no ha comenzado");
            }
        }
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

        boolean badRecord=false;

        Player tryPlayer = Bukkit.getPlayer(uuid);
        if(tryPlayer!=null) U.playsoundTarget(tryPlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);

        BukkitRunnable mainRun = mapaRunnables.remove(uuid);
        if(mainRun!=null){
            try {
                if(!mainRun.isCancelled()) mainRun.cancel();
            } catch (IllegalStateException ignored) {badRecord=true;}
        }
        //caso de que se grabó mal
        else badRecord=true;

        // destacar END en record
        if(!(mapaOnGoingRecords.containsKey(uuid))) return;
        String fileName = mapaOnGoingRecords.remove(uuid).getKey();

        CustomConfig file = getFile(fileName, false);

        if(file==null) return;
        if(badRecord){
            File rawFile = getFileRaw(fileName, false);
            if(rawFile==null) return;
            U.mensajeConsolaNP("&eSe ha detectado una cinemática vacia/corrompida! borrando a &6"+fileName);
            rawFile.delete();
            return;
        }

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
    @Nullable
    private static File getFileRaw(String name, boolean forceCreate){
        File archivo = new File(DottUtils.folderCinematic, name+".yml");
        boolean success = archivo.exists();
        if(!success){
            if(forceCreate){
                createNewFile(name);
                return new File(DottUtils.folderCinematic, name+".yml");

            }else{return null;}
        }
        return archivo;
    }

    private static boolean checkFileExists(String name){
        CustomConfig config = getFile(name, false);
        return !(config==null);
    }

    //------------REPRODUCE THINSG---------------------

    public static void reproduceCinematicForAll(String fileName, boolean clonePlayer) throws CinematicFileDontExist{
        for(Player p : Bukkit.getOnlinePlayers()){
            reproduceCinematic(p, fileName, clonePlayer, true);
        }
    }

    // returns if success
    public static boolean reproduceCinematicBoolean(Player p, String fileName, boolean clonePlayer){
        boolean result = true;
        try{
            reproduceCinematic(p, fileName, clonePlayer, true);
        } catch (CinematicFileDontExist e) {
            result=false;
        }
        return result;
    }
    //natural=false -> ONLY INTERNAL
    public static void reproduceCinematic(Player p, String fileName, boolean clonePlayer, boolean natural) throws CinematicFileDontExist{
        CustomConfig config = getFile(fileName, false);
        if (!(checkFileExists(fileName)) || config == null){
            throw new CinematicFileDontExist("No se puede reproducir animación, pues posiblemente no existe.", "Cinematics/"+fileName);
        }

        UUID uuid = p.getUniqueId();
        checkAndStop(uuid);
        if(natural) stopReproducing(uuid);
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
            npc.setPersistent(true);
            npc.setInvulnerable(true);

            npcToDeliver = npc;
        }

        Location playerLoc = p.getLocation();
        Map.Entry<GameMode, Location> entryToDeliver = new AbstractMap.SimpleEntry<>(gmToDeliver, playerLoc);

        if(natural) mapaPlayerData.put(uuid, entryToDeliver); // DATA IMPORTANTE

        U.hidePlayerForAll(p);
        p.setGameMode(GameMode.SPECTATOR);

        // textDisplay
        TextDisplay textDisplay = (TextDisplay) playerLoc.getWorld().spawnEntity(playerLoc, EntityType.TEXT_DISPLAY);
        textDisplay.setVisibleByDefault(true);
        textDisplay.setPersistent(true);
        textDisplay.setInvulnerable(true);

        if(!natural){
            npcToDeliver = mapaPlayerDataTwo.get(uuid).getKey();
            notNaturalClean(uuid);
        }

        Map.Entry<Mannequin, TextDisplay> entryToDeliverTwo = new AbstractMap.SimpleEntry<>(npcToDeliver, textDisplay);
        mapaPlayerDataTwo.put(uuid, entryToDeliverTwo); //DATADOS

        long period = config.getConfig().getLong("Period");
        int count = 0;
        textDisplay.setInterpolationDelay(0);
        textDisplay.setInterpolationDuration((int) period);
        textDisplay.setTeleportDuration((int) period);

        List<BukkitRunnable> lRunnables = new ArrayList<>();

        for(String input : config.getConfig().getStringList("Locations")){
            if(checkFileExists(input)){
                BukkitRunnable bringToAnotherRunnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        reproduceCinematic(p, input, false, false);
                    }
                };

                bringToAnotherRunnable.runTaskLater(plugin, count*period);
                lRunnables.add(bringToAnotherRunnable);
                break;
            }

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
            if(aI[0].equalsIgnoreCase("title")){ // title0;mensaje1;submensaje2;10;20;10
                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (aI[2].equalsIgnoreCase("null")) aI[2] = "";
                        int fadeIn=Integer.parseInt(aI[3]), stay=Integer.parseInt(aI[4]), fadeOut=Integer.parseInt(aI[5]);

                        U.sendTitleTarget(p, aI[1], aI[2], fadeIn, stay, fadeOut);
                    }
                };
                task.runTaskLater(plugin, count*period);
                lRunnables.add(task);
                continue;
            }

            World world = Bukkit.getWorld(aI[0]);
            double x = Double.parseDouble(aI[1]), y = Double.parseDouble(aI[2]), z = Double.parseDouble(aI[3]);
            float yaw = Float.parseFloat(aI[4]), pitch = Float.parseFloat(aI[5]);

            Location loc = new Location(world, x, y, z, yaw, pitch);

            if(count==0){
                p.setSpectatorTarget(null);
                textDisplay.teleport(loc);

                if(npcToDeliver!=null){
                    if(!(npcToDeliver.getWorld().equals(world))){
                        npcToDeliver.remove();
                    }
                }

                p.teleport(loc);
                count++;
                continue;
            }
            // MAINNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    p.setSpectatorTarget(null);
                    textDisplay.teleport(loc);
                    p.setSpectatorTarget(textDisplay);
                }
            };

            task.runTaskLater(plugin, count*period);
            lRunnables.add(task);
            count++;
        }

        mapaPlayerReproduce.put(uuid, lRunnables);
    }
    private static void notNaturalClean(UUID uuid){
        Map.Entry<Mannequin, TextDisplay> entry = mapaPlayerDataTwo.remove(uuid);
        entry.getValue().remove();
    }

    public static void stopReproducingForAll(){
        for(Player p : Bukkit.getOnlinePlayers()){
            stopReproducing(p.getUniqueId());
        }
    }
    public static void stopReproducing(UUID uuid){
        if(!(mapaPlayerData.containsKey(uuid))) return;

        Player player = Bukkit.getPlayer(uuid);
        boolean online = !(player==null);

        //DATA
        Map.Entry<GameMode, Location> eData = mapaPlayerData.remove(uuid);
        Map.Entry<Mannequin, TextDisplay> eDataTwo = mapaPlayerDataTwo.remove(uuid);

        Location oldLoc = eData.getValue();

        TextDisplay textDisplay = eDataTwo.getValue();
        textDisplay.remove();


        //DATA2


        Mannequin npc = eDataTwo.getKey();

        //ups
        if(online)player.teleport(oldLoc);
        else{
            Map.Entry<GameMode, Location> hmToDeliver = new AbstractMap.SimpleEntry<GameMode, Location>(eData.getKey(), oldLoc);
            mapaPending.put(uuid, hmToDeliver);
        }

        if(npc!=null&&online){
            player.setSpectatorTarget(npc);
            if(npc.isInWorld()){
                npc.remove();
            }else{
                Location spawnLoaded = new Location(Bukkit.getWorlds().getFirst(), 0,0,0);
                npc.spawnAt(spawnLoaded);
                Bukkit.getScheduler().runTaskLater(plugin, npc::remove, 10L);
            }
        }

        if(online) player.setGameMode(eData.getKey());
        if(online&&eData.getKey().equals(GameMode.SPECTATOR)) player.setSpectatorTarget(player);

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
        if(!(mapaPending.containsKey(uuid))) return;

        Map.Entry<GameMode, Location> entry = mapaPending.remove(uuid);
        Location locToTp = entry.getValue();
        player.teleport(locToTp);
        player.setGameMode(entry.getKey());
    }
    public static void onDisableCheck(){
        for(Player p : Bukkit.getOnlinePlayers()){
            UUID uuid = p.getUniqueId();
            CinematicsConfig.checkAndStop(uuid);
            CinematicsConfig.stopReproducing(uuid);
        }
    }

    //
    static void cineMsg(String msg, Player p){
        String prefix = U.getMsgPath("cinematic_prefix", "&6&l[&e&lCinematics&6&l] ");
        U.targetMessageNP(p, prefix+msg);
    }
    static void cineMsg(String msg, CommandSender sender){
        String prefix = U.getMsgPath("cinematic_prefix", "&6&l[&e&lCinematics&6&l] ");
        U.targetMessageNP(sender, prefix+msg);
    }
    //
    public static List<String> getCinematicsName(){
       File folder = DottUtils.folderCinematic;
       List<String> aList = new ArrayList<>();

       String[] names = folder.list();
       if(names==null) return aList;

        aList.addAll(Arrays.asList(names));

        aList.replaceAll(name ->
                name.endsWith(".yml") ? name.substring(0, name.length() - 4) : name
        );
        return aList;
    }
}
