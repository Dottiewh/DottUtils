package mp.dottiewh.music;

import mp.dottiewh.DottUtils;
import mp.dottiewh.config.Config;
import mp.dottiewh.config.CustomConfig;
import mp.dottiewh.items.Exceptions.ItemSectionEmpty;
import mp.dottiewh.music.Exceptions.InvalidMusicConfigException;
import mp.dottiewh.music.Exceptions.MusicNullKeyException;
import mp.dottiewh.music.Exceptions.MusicSectionEmpty;
import mp.dottiewh.music.Exceptions.MusicSoundException;
import mp.dottiewh.utils.U;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class MusicConfig {
    private static Plugin pl;
    private static File mFolder;
    private static DottUtils instance;
    private final static Map<UUID, List<BukkitRunnable>> mRunnableList = new HashMap<>();

    private static float volume = 1;

    public static void initMusicConfig(){
        mFolder = DottUtils.folderMusic;
        instance=DottUtils.getInstance();
        pl=DottUtils.getPlugin();
        MusicMainCommand.setMusicPrefix(U.getMsgPath("music_prefix", "&d&l[&9&lMusica&d&l] "));
    }
    private static void addRunnable(UUID uuid, BukkitRunnable runnable, int delay){
        mRunnableList.computeIfAbsent(uuid, k -> new ArrayList<>()).add(runnable);

        runnable.runTaskLater(pl, delay);
    }
    public static void stopMusicTasks(){
        Iterator<Map.Entry<UUID, List<BukkitRunnable>>> it =
                mRunnableList.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<UUID, List<BukkitRunnable>> entry = it.next();
            for (BukkitRunnable runnable : entry.getValue()) {
                runnable.cancel();
            }
            it.remove();
        }
    }

    public static void stopMusicTasks(UUID uuid){
        List<BukkitRunnable> list = mRunnableList.remove(uuid);
        if (list == null) return;

        for (BukkitRunnable runnable : list) {
            runnable.cancel();
        }
    }

    public static void reproduceToAll(String song, boolean loop){
        for(Player player : Bukkit.getOnlinePlayers()){
            reproduceTo(song, player, loop);
        }
    }
    public static void reproduceTo(String song, Player player, boolean loop) throws InvalidMusicConfigException{
        File file = getFileRaw(song);
        if(file==null){
            throw new MusicSectionEmpty(song, "No se ha identificado la canción.");
        }

        CustomConfig c = getFile(song);
        ConfigurationSection section = c.getConfig().getConfigurationSection(song);
        if(section==null){
            throw new InvalidMusicConfigException(song, "No existe la sección en un yml.");
        }


        ConfigurationSection strSection = section.getConfigurationSection("Structure");
        if(strSection==null){
            throw new MusicSectionEmpty(song+".Structure", "Structure no existe en el yml?");
        }

        stopMusicTasks(player.getUniqueId());
        coreReproduce(song, strSection, player, loop);
    }
    private static void coreReproduce(String songName, ConfigurationSection strSection, Player player, boolean loop){
        int partDuration = 0, i=1;
        int totalParts = strSection.getKeys(false).size();

        BukkitRunnable taskIfLast = new BukkitRunnable() {
            @Override
            public void run() {
                stopMusicTasks(player.getUniqueId());
            }
        };
        BukkitRunnable taskIfLastAndLoop = new BukkitRunnable() {
            @Override
            public void run() {
                stopMusicTasks(player.getUniqueId());
                coreReproduce(songName, strSection, player, loop);
            }
        };


        for(String sPart : strSection.getKeys(false)){
            boolean isLast = (i==totalParts);
            ConfigurationSection partSection = strSection.getConfigurationSection(sPart);
            if(partSection==null){
                throw new MusicSectionEmpty(strSection.getCurrentPath()+sPart, "No se ha identificado la sección parte de: "+sPart);
            }

            int duration = partSection.getInt("ticks_to_continue", 0);

            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    for(String sSection : partSection.getStringList("section_list")){
                        sectionLoad(songName, sSection, duration, player);
                    }

                }
            };
            addRunnable(player.getUniqueId(), runnable, partDuration);
            partDuration = partDuration + duration;
            if(isLast){
                if(loop){
                    addRunnable(player.getUniqueId(), taskIfLastAndLoop, partDuration);
                }else{
                    addRunnable(player.getUniqueId(), taskIfLast, partDuration);
                }
            }

            i++;
        }
    }

    private static void sectionLoad(String songName, String sectionName, int totalTicks, Player player){
        if(totalTicks<=0) totalTicks=1;

        CustomConfig c = getFile(songName);
        ConfigurationSection section = c.getConfig().getConfigurationSection(songName+".Sections");
        if(section==null){
            throw new MusicSectionEmpty(songName+".Sections", "No se ha podido entrar en Sections, no existe Sections en tu canción?");
        }

        int ticksDuration=0;
        for(String input : section.getStringList(sectionName)){
            String[] aInput = input.split(";", 3);

            //U.mensajeConsolaNP(input+" | "+ Arrays.toString(aInput));

            if(aInput.length<2) {
                //U.mensajeConsolaNP("continue args -2");
                continue;
            }
            if(!(aInput[0].equalsIgnoreCase("wait"))){
                //U.mensajeConsolaNP("continue no es wait");
                continue;
            }
            int localTicks = Integer.parseInt(aInput[1]);

            //U.mensajeConsolaNP(input+" | "+ Arrays.toString(aInput)+" | "+localTicks);

            ticksDuration = ticksDuration+localTicks;
        }
        if(ticksDuration==0){
            throw new ArithmeticException("Comprueba si tienes algún wait en tu canción.");
        }

        int repeatTimes = (int) totalTicks/ticksDuration;

        int accumuledDelay = 0; // in ticks

        for(int i=0;i<repeatTimes;i++){

            for(String input : section.getStringList(sectionName)){

                String[] aInput = input.split(";", 3);
                if(aInput.length<2) continue;
                if(aInput[0].equalsIgnoreCase("wait")){
                    accumuledDelay = accumuledDelay+Integer.parseInt(aInput[1]);
                    continue;
                }
                if(aInput.length<3) continue;
                //end of "checks"?

                String sID = aInput[0];
                String sVol = aInput[1];
                String sPitch = aInput[2];

                NamespacedKey key = NamespacedKey.fromString(sID.toLowerCase());
                if(key==null){
                    throw new MusicNullKeyException(songName, "No se ha podido obtener el NamespacedKey tras un input de: "+sID.toLowerCase());
                }

                Sound sound = Registry.SOUNDS.get(key);
                if(sound==null){
                    throw new MusicSoundException(songName, "No se ha podido transformar a Sound el key de: "+key.toString());
                }


                float vol = Float.parseFloat(sVol);
                float pitch = Float.parseFloat(sPitch);


                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        float finalVol = vol*(volume*0.125f);
                        player.playSound(player, sound, finalVol, pitch);

                    }
                };

                addRunnable(player.getUniqueId(), runnable, accumuledDelay);
            }
        }
    }

    private static CustomConfig getFile(String name){
        CustomConfig config = new CustomConfig(name+".yml", "musics", instance, false);

        config.registerConfig();
        return config;
    }
    @Nullable
    private static File getFileRaw(String name){
        File raw = new File(DottUtils.folderMusic, name+".yml");
        if(!raw.exists()){
            return null;
        }
        return raw;
    }
    @NotNull
    public static List<String> getMusicList(){
        String[] childs = mFolder.list();

        if (childs==null) throw new MusicSectionEmpty("Problema en tu carpeta de musics, Maybe folder or childs doesn't exists.");

        return U.removeYmlFormat(Arrays.asList(childs));
    }

    @NotNull
    public static Material getDisplayMaterial(String songName) throws MusicSectionEmpty{
        if(getFileRaw(songName)==null) throw new MusicSectionEmpty("No existe la canción al intentar conseguir el display material.");
        CustomConfig cConfig = getFile(songName);
        String stringMaterial = cConfig.getConfig().getString(songName+".DisplayMaterial", null);
        if(stringMaterial==null){
            U.mensajeDebugConsole("&cMaterial captado en "+songName+" es null?");
            return Material.YELLOW_DYE;
        }
        Material material = Material.matchMaterial(stringMaterial.toUpperCase());
        if(material==null){
            U.mensajeConsola("&c"+songName+" tiene un DisplayMaterial no valido: "+stringMaterial);
            return Material.YELLOW_DYE;
        }
        return material;
    }
    public static void setVolume(float vol){
        if(vol>2){
            volume=2;
            return;
        }
        if(vol<0){
            volume=0;
            return;
        }
        volume=vol;
    }
    public static void addVolume(float f){
        setVolume(volume+f);
    }
    static float getVolume(){
        return volume;
    }

}
