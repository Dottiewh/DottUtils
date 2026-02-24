package mp.dottiewh.music.classes;

import mp.dottiewh.DottUtils;
import mp.dottiewh.config.CustomConfig;
import mp.dottiewh.music.exceptions.MusicSectionEmpty;
import mp.dottiewh.music.MusicConfig;
import mp.dottiewh.utils.U;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class LegacyMusic {
    //ConfigurationSection section;

    String id;
    String titleAndAuthor;
    int ticksDuration;
    Material displayMaterial;

    private static final HashMap<String, LegacyMusic> cacheMap = new HashMap<>();
    protected static final Plugin plugin = DottUtils.getPlugin();

    public LegacyMusic(String songName, String titleHex, String middleHex, String authorHex) throws MusicSectionEmpty{

        //if(MusicConfig.getFileRaw(songName)==null) throw new MusicSectionEmpty("No existe la canción "+songName+" al intentar conseguir el datos.");
        CustomConfig cConfig = MusicConfig.getFile(songName);
        if(!cConfig.getFile().exists()) throw new MusicSectionEmpty("No existe la canción "+songName+" al intentar conseguir el datos.");
        ConfigurationSection mainSec = cConfig.getConfig().getConfigurationSection(songName);
        if (mainSec==null) throw new MusicSectionEmpty("No existe la sección '"+songName+"' en la canción "+songName);

        //this.section=mainSec;

        this.id=songName;
        this.titleAndAuthor=MusicConfig.getDisplayNameAndAuthorHex(mainSec, titleHex, middleHex, authorHex);
        ConfigurationSection structureSection = mainSec.getConfigurationSection("Structure");
        if (structureSection==null) throw new MusicSectionEmpty("No existe la sección de 'Structure' en la canción "+songName);
        this.ticksDuration=MusicConfig.getTicksDuration(structureSection);

        this.displayMaterial=MusicConfig.getDisplayMaterial(mainSec, songName);

        //U.mensajeDebugConsole(this.toString());
    }

    public static void loadCache(){
        cacheMap.clear();
        U.mensajeDebugConsole("Caché Started - MUSIC");
        List<String> musicList = MusicConfig.getMusicList();
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                for(String musicName : musicList){
                    LegacyMusic legacyMusic = new LegacyMusic(musicName, "#bb67e6", "#2b2b2b", "#5e6aeb");
                    legacyMusic.addToCache();
                }
            }
        };

        if(DottUtils.isCacheAsync()) runnable.runTaskAsynchronously(plugin);
        else runnable.run();
        U.mensajeDebugConsole("Caché End - MUSIC");
    }

    public void addToCache(){
        cacheMap.put(id, this);
    }

    @Nullable
    public static LegacyMusic getFromCache(String songName){
        return cacheMap.get(songName);
    }
    //
    /*public ConfigurationSection getSection() {
        return section;
    }*/

    public String getTitleAndAuthor() {
        return titleAndAuthor;
    }

    public int getTicksDuration() {
        return ticksDuration;
    }

    public Material getDisplayMaterial() {
        return displayMaterial;
    }

    @Override
    public String toString() {
        return "LegacyMusic{" +
                "id='" + id + '\'' +
                ", titleAndAuthor='" + titleAndAuthor + '\'' +
                ", ticksDuration=" + ticksDuration +
                ", displayMaterial=" + displayMaterial +
                '}';
    }
}
