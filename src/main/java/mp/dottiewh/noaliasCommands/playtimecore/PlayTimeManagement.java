package mp.dottiewh.noaliasCommands.playtimecore;

import mp.dottiewh.DottUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PlayTimeManagement {
    private static final int minutesToSave = 1;
    private static final long finalCooldown = 20L*(minutesToSave*60);

    private static final Map<String, Integer> playtimeMap = new ConcurrentHashMap<>();

    //-----------
    public static void onJoinManagement(PlayerJoinEvent event){
        String name = event.getPlayer().getName();
        if(isNameInConfig(name)){
            int hisValue = getNameValue(name);
            playtimeMap.put(name, hisValue);
        }else{
            playtimeMap.put(name, 0);
        }
    }
    public static void onLeaveManagement(PlayerQuitEvent event){
        String name = event.getPlayer().getName();
        setValue(name, playtimeMap.get(name));
        playtimeMap.remove(name);
    }
    public static void onEnableManagement(){
        Bukkit.getScheduler().runTaskTimer(DottUtils.getPlugin(), ()->{
            playtimeMap.replaceAll((name, val) -> val + 1);
        }, finalCooldown, finalCooldown); //final cooldown = cada 1 minuto
        //
        Bukkit.getScheduler().runTaskTimer(DottUtils.getPlugin(), () -> {
            playtimeMap.forEach(PlayTimeManagement::setValue);
        }, 20L*60*5, 20L*60*5);
    }
    //
    //
    //some other things getters setters checks
    private static void setValue(String name, int value){
        DottUtils.ymlPlayTime.getConfig().set(name, value);
        DottUtils.ymlPlayTime.saveConfig();
    }

    public static boolean isNameInConfig(String name){
        int minutes = DottUtils.ymlPlayTime.getConfig().getInt(name, -1);
        return minutes != -1;
    }
    public static int getNameValue(String name){
        int value = DottUtils.ymlPlayTime.getConfig().getInt(name, -1);
        assert value != -1;
        return value;
    }
    public static List<Map.Entry<String, Integer>> getTop(int max){
        FileConfiguration config = DottUtils.ymlPlayTime.getConfig();
        Map<String, Integer> tempMap = new HashMap<>();
        //load
        for(String key : config.getKeys(false)){
            int value = config.getInt(key, 0);
            tempMap.put(key, value);
        }

        return tempMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(max)
                .toList();
    }
}
