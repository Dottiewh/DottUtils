package mp.dottiewh.cinematics;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CinematicPlayerData {
    private Player player = null;
    private UUID uuid = null;

    private GameMode previousGameMode = null; // PlayerData getKey
    private Location previousLoc = null; // PlayerData getValue
    private Mannequin mannequin = null; // PlayerData2 getKey
    private TextDisplay textDisplay = null; // PlayerData2 getValue

    private List<BukkitTask> runnableList = new ArrayList<>(); // list<> mapaPlayerReproduce

    private CinematicPlayerData(){}
    public static CinematicPlayerData getInstance(){
        return new CinematicPlayerData();
    }
    public static CinematicPlayerData getInstance(Player player){
        CinematicPlayerData building = new CinematicPlayerData();
        building.setPlayer(player).setUuid(player.getUniqueId());
        return building;
    }


    public CinematicPlayerData addRunnable(BukkitTask bTask){
        runnableList.add(bTask);
        return this;
    }
    public CinematicPlayerData addRunnables(Collection<? extends BukkitTask> tasks){
        runnableList.addAll(tasks);
        return this;
    }
    //__________________________----------------------------===================
    public Player getPlayer() {
        return player;
    }
    public UUID getUuid() {
        return uuid;
    }
    public GameMode getPreviousGameMode() {
        return previousGameMode;
    }
    public Location getPreviousLoc() {
        return previousLoc;
    }
    public Mannequin getMannequin() {
        return mannequin;
    }
    public TextDisplay getTextDisplay() {
        return textDisplay;
    }
    public List<BukkitTask> getRunnableList() {
        return runnableList;
    }

    public CinematicPlayerData setPlayer(Player player) {
        this.player = player;
        return this;
    }
    public CinematicPlayerData setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }
    public CinematicPlayerData setPreviousGameMode(GameMode previousGameMode) {
        this.previousGameMode = previousGameMode;
        return this;
    }
    public CinematicPlayerData setPreviousLoc(Location previousLoc) {
        this.previousLoc = previousLoc;
        return this;
    }
    public CinematicPlayerData setMannequin(Mannequin mannequin) {
        this.mannequin = mannequin;
        return this;
    }
    public CinematicPlayerData setTextDisplay(TextDisplay textDisplay) {
        this.textDisplay = textDisplay;
        return this;
    }
    public CinematicPlayerData setRunnableList(List<BukkitTask> runnableList) {
        this.runnableList = runnableList;
        return this;
    }

}
