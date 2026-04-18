package mp.dottiewh.api.directors;

import mp.dottiewh.cinematics.CinematicsConfig;
import mp.dottiewh.music.MusicConfig;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class DuCinematicsDirector {
    public DuCinematicsDirector(){}


    public void playCinematic(Player p, String fileName, boolean clonePlayer){
        CinematicsConfig.reproduceCinematic(p, fileName, clonePlayer, true);
    }
    public void playCinematicForAll(String fileName, boolean clonePlayer){
        CinematicsConfig.reproduceCinematicForAll(fileName, clonePlayer);
    }
    public void stopCinematic(UUID uuid){
        CinematicsConfig.stopReproducing(uuid);
    }
    public void stopCinematicForAll(){
        CinematicsConfig.stopReproducingForAll();
    }

    @Nullable
    public List<String> getCinematicsList(){
        return CinematicsConfig.getCinematicsName();
    }
}
