package mp.dottiewh.music;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import mp.dottiewh.DottUtils;
import mp.dottiewh.config.CustomConfig;
import mp.dottiewh.music.Exceptions.*;
import mp.dottiewh.music.classes.Layer;
import mp.dottiewh.music.classes.Music;
import mp.dottiewh.music.classes.Note;
import mp.dottiewh.music.classes.ResolvedNote;
import mp.dottiewh.utils.NBSutils;
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
import java.io.IOException;
import java.util.*;

public class MusicConfig {
    private static Plugin pl;
    private static File mFolder;
    private static DottUtils instance;
    private final static Map<UUID, List<BukkitRunnable>> mRunnableList = new HashMap<>();

    private static float volume = 1;
    public enum MusicRoundType {
        FLOOR,
        NORMAL,
        CELLING
    }

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

        List<String> list = new ArrayList<>(Arrays.asList(childs));

        list.removeIf(s -> s.equals("import"));
        U.removeYmlFormat(list);
        return list;
    }

    @NotNull
    public static int getTicksDuration(String songName) throws MusicSectionEmpty{
        if(getFileRaw(songName)==null) throw new MusicSectionEmpty("No existe la canción "+songName+" al intentar conseguir el tiempo.");
        CustomConfig cConfig = getFile(songName);

        ConfigurationSection structureSection = cConfig.getConfig().getConfigurationSection(songName+".Structure");
        if (structureSection==null) throw new MusicSectionEmpty("No existe la sección 'Structure' en la canción "+songName);

        int tickCount=0;
        for(String key : structureSection.getKeys(false)){
            ConfigurationSection partSection = structureSection.getConfigurationSection(key);
            if(partSection==null) continue;
            tickCount+=partSection.getInt("ticks_to_continue", 0);
        }
        return tickCount;
    }
    @NotNull
    public static Material getDisplayMaterial(String songName) throws MusicSectionEmpty{
        if(getFileRaw(songName)==null) throw new MusicSectionEmpty("No existe la canción "+songName+" al intentar conseguir el display material.");
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
    //
    public static void importNBSFile(String fileName) throws MusicFileRelatedException {
        importNBSFile(fileName, MusicRoundType.NORMAL);
    }
    public static void importNBSFile(String fileName, MusicRoundType roundType) throws MusicFileRelatedException {
        File importFolder = new File(mFolder, "import");
        if(!importFolder.exists()) importFolder.mkdir();
        File nbsFile = new File(importFolder, fileName+".nbs");

        if(!nbsFile.exists()) throw new MusicFileRelatedException("El file "+fileName+".nbs no existe o no se puede captar.", "route: "+nbsFile.getPath());
        Music music = NBSutils.decodeNBS(nbsFile);
        if(music==null) return;

        fileName = fileName.replace(" ","_");
        fileName=fileName.replace(".","-");

        File newFile = new File(mFolder, fileName+".yml");
        if(newFile.exists()) throw new MusicFileRelatedException("Ya existe un file "+fileName+" registrado en tu carpeta music/");
        try{
            newFile.createNewFile();
        }catch (IOException e){
            U.mensajeConsolaNP(e.toString());
        }
        U.mensajeConsolaNP("&eArchivo creado");

        CustomConfig customConfigNewFile = new CustomConfig(fileName+".yml", "musics", DottUtils.getInstance(), false);
        customConfigNewFile.registerConfig();
        FileConfiguration config = customConfigNewFile.getConfig();

        //definir cosas
        List<Layer> layerList = music.getLayerList();
        int normalizedTempo = normalizeTempo(music.getSongTempo(), roundType);
        int tickDuration = music.getSongLength()*normalizedTempo;

        ConfigurationSection mainSection = config.createSection(fileName);
        U.mensajeConsolaNP("&eSe ha entrado en el archivo correctamente.");

        mainSection.set("DisplayMaterial", Material.GOLD_INGOT.name());
        U.mensajeConsolaNP("&eDisplayMaterial set");
        //-------STRUCTURE-------------
        ConfigurationSection structureSection = mainSection.createSection("Structure");
        ConfigurationSection mainPartSection = structureSection.createSection("MainPart");

        List<String> layerListNames = new ArrayList<>();
        layerList.forEach(layer->{layerListNames.add(layer.getName().replace(" ","_").replace(".","-"));});
        for (int i = 0; i < layerListNames.size(); i++) {
            String value = layerListNames.get(i);
            if (value == null || value.isEmpty()) {
                layerListNames.set(i, "empty_" + i);
            }
        }

        HashMap<String, Integer> counter = new HashMap<>();

        //checkea si hay una copia ya
        for (int i = 0; i < layerListNames.size(); i++) {
            String value = layerListNames.get(i);
            int count = counter.getOrDefault(value, 0);

            if(count>0) layerListNames.set(i, value + "copy".repeat(count));

            counter.put(value, count+1);
        }
        try{
            mainPartSection.set("section_list", layerListNames);

            U.mensajeConsolaNP("&eParte de las secciones en structure listo.");
            //------SECTIONS------------
            ConfigurationSection partsSections = mainSection.createSection("Sections");

            RegistryAccess regAccess = RegistryAccess.registryAccess();
            Registry<@NotNull Sound> registry = regAccess.getRegistry(RegistryKey.SOUND_EVENT);

            int countCheck=0;
            for(Layer layer : layerList){
                List<Note> noteList = layer.getNotes();
                List<String> outputList = new ArrayList<>();
                byte layerVolume = layer.getVolume();
                String layerName = layer.getName().replace(" ", "_");
                String expectedValue = layerListNames.get(countCheck);
                if(!expectedValue.equalsIgnoreCase(layerName)) layerName=expectedValue;

                layerName=layerName.replace(".","-");

                int lastNoteTick=0;
                for(Note note : noteList){
                    ResolvedNote resolvedNote = new ResolvedNote(note, layerVolume);
                    int actualTick = resolvedNote.getTick()*normalizedTempo;
                    if(lastNoteTick!=actualTick){
                        int delta = actualTick-lastNoteTick;
                        outputList.add("wait;"+delta);
                    }
                    lastNoteTick=actualTick;

                    Sound sound = resolvedNote.getSound();
                    NamespacedKey namespacedKey = registry.getKey(sound);
                    if(namespacedKey==null){
                        U.mensajeConsolaNP("&cNo se ha podido resolver el namespacedkey "+namespacedKey+", saltandolo...");
                        continue;
                    }
                    String keyString = namespacedKey.getKey();

                    if(actualTick>tickDuration) tickDuration=actualTick;
                    outputList.add(keyString+";"+resolvedNote.getVolume()+";"+resolvedNote.getPitch());
                }
                //structure parte 2
                mainPartSection.set("ticks_to_continue", tickDuration);

                partsSections.set(layerName, outputList);
                countCheck++;
                U.mensajeConsolaNP("&eSección "+layerName+" setteada.");
            }
            U.mensajeDebugConsole("NTempo: &e"+normalizedTempo);
        }catch (Exception e){
            U.mensajeConsolaNP("&cSe ha captado un error.");
            U.mensajeConsolaNP("&4"+e.toString());
            U.mensajeConsolaNP("&c"+Arrays.toString(e.getStackTrace()));
            newFile.delete();
            return;
        }

        //======================
        customConfigNewFile.saveConfig();
        U.mensajeConsolaNP("&aSe ha importado "+fileName+" correctamente! &8| &6RoundType: &e"+roundType);
    }

    private static int normalizeTempo(float tempo, MusicRoundType roundType){
        int factor=1;
        switch (roundType){
            case CELLING -> {
                factor =  (int) Math.ceil(20d/tempo);

            }
            case FLOOR -> {
                factor = U.removeDecimals(20d/tempo);
            }
            default -> {
                factor = Math.round(20f/tempo);
            }
        }
        //
        U.mensajeDebugConsole("original tempo: "+tempo);
        if(factor==0) return 1;
        return factor;
    }

}
