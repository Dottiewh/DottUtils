package mp.dottiewh.api.directors;

import mp.dottiewh.music.MusicConfig;
import mp.dottiewh.music.classes.LegacyMusic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class DuMusicsDirector {
    public DuMusicsDirector(){}



    public void playSong(String song, Player player, boolean loop){
        MusicConfig.reproduceTo(song, player, loop);
    }
    public void playSongForAll(String song, boolean loop){
        MusicConfig.reproduceToAll(song, loop);
    }
    public void stopSong(){
        MusicConfig.stopMusicTasks();
    }
    public void stopSong(UUID uuid){
        MusicConfig.stopMusicTasks(uuid);
    }

    public void setGlobalVolume(float f){
        MusicConfig.setGlobalVolume(f);
    }
    public void addGlobalVolume(float f){
        MusicConfig.addVolume(f);
    }
    public float getGlobalVolume(){
        return MusicConfig.getGlobalVolume();
    }

    @NotNull
    public List<String> getMusicsList(){
        return MusicConfig.getMusicList();
    }
    public boolean existsMusic(String id){
        return MusicConfig.existsMusic(id);
    }

    @Nullable
    public LegacyMusic getLegacyMusicFromCache(String id){
        return LegacyMusic.getFromCache(id);
    }

}
