package mp.dottiewh.cinematics;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CinematicRecordPlayerData {
    private Player player = null;
    private UUID uuid = null;

    private BukkitRunnable mainRunnable = null; // list<> mapaRunnables
    private BukkitTask countdownTask = null; // mapaCountdown

    private String recordFileName = null; // mapaOnGoingRecords.getKey
    private Long recordPeriod; // mapaOnGoingRecords.getValue

    private CinematicsConfig.Status status = null; // mapaPlayerStatus

    private CinematicRecordPlayerData(){}
    public static CinematicRecordPlayerData getInstance(){
        return new CinematicRecordPlayerData();
    }
    public static CinematicRecordPlayerData getInstance(Player player){
        CinematicRecordPlayerData building = new CinematicRecordPlayerData();
        building.setPlayer(player).setUuid(player.getUniqueId());
        return building;
    }


    //===

    public Player getPlayer() {
        return player;
    }
    public UUID getUuid() {
        return uuid;
    }

    public BukkitRunnable getMainRunnable() {
        return mainRunnable;
    }

    public BukkitTask getCountdownTask() {
        return countdownTask;
    }

    public String getRecordFileName() {
        return recordFileName;
    }
    public Long getRecordPeriod() {
        return recordPeriod;
    }
    public CinematicsConfig.Status getStatus() {
        return status;
    }

    public CinematicRecordPlayerData setPlayer(Player player) {
        this.player = player;
        return this;
    }
    public CinematicRecordPlayerData setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public CinematicRecordPlayerData setMainRunnable(BukkitRunnable mainRunnable) {
        this.mainRunnable = mainRunnable;
        return this;
    }

    public CinematicRecordPlayerData setCountdownTask(BukkitTask countdownTask) {
        this.countdownTask = countdownTask;
        return this;
    }

    public CinematicRecordPlayerData setRecordFileName(String recordFileName) {
        this.recordFileName = recordFileName;
        return this;
    }
    public CinematicRecordPlayerData setRecordPeriod(Long recordPeriod) {
        this.recordPeriod = recordPeriod;
        return this;
    }
    public CinematicRecordPlayerData setStatus(CinematicsConfig.Status status) {
        this.status = status;
        return this;
    }
}
