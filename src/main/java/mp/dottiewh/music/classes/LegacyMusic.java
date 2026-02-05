package mp.dottiewh.music.classes;

import mp.dottiewh.config.CustomConfig;
import mp.dottiewh.music.exceptions.MusicSectionEmpty;
import mp.dottiewh.music.MusicConfig;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class LegacyMusic {
    ConfigurationSection section;

    String titleAndAuthor;
    int ticksDuration;
    Material displayMaterial;

    public LegacyMusic(String songName, String titleFormat, String authorFormat) throws MusicSectionEmpty{

        if(MusicConfig.getFileRaw(songName)==null) throw new MusicSectionEmpty("No existe la canción "+songName+" al intentar conseguir el datos.");
        CustomConfig cConfig = MusicConfig.getFile(songName);
        ConfigurationSection mainSec = cConfig.getConfig().getConfigurationSection(songName);
        if (mainSec==null) throw new MusicSectionEmpty("No existe la sección '"+songName+"' en la canción "+songName);

        this.section=mainSec;

        this.titleAndAuthor=MusicConfig.getDisplayNameAndAuthor(mainSec, titleFormat, authorFormat);
        ConfigurationSection structureSection = mainSec.getConfigurationSection("Structure");
        if (structureSection==null) throw new MusicSectionEmpty("No existe la sección de 'Structure' en la canción "+songName);
        this.ticksDuration=MusicConfig.getTicksDuration(structureSection);

        this.displayMaterial=MusicConfig.getDisplayMaterial(mainSec, songName);

        //U.mensajeDebugConsole(this.toString());
    }


    public ConfigurationSection getSection() {
        return section;
    }

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
                "section=" + section +
                ", titleAndAuthor='" + titleAndAuthor + '\'' +
                ", ticksDuration=" + ticksDuration +
                ", displayMaterial=" + displayMaterial +
                '}';
    }
}
