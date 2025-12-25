package mp.dottiewh.api;

import mp.dottiewh.DottUtils;
import mp.dottiewh.cinematics.CinematicsConfig;
import mp.dottiewh.config.Config;
import mp.dottiewh.music.MusicConfig;
import mp.dottiewh.utils.U;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DuApiManager {
    Plugin plugin;
    String prefix;

    public DuApiManager(Plugin plugin, String prefix) {
        this.plugin = plugin;
        this.prefix = prefix;

        U.mensajeConsolaNP(prefix+"se ha suscrito correctamente a la api de "+ DottUtils.prefix);
    }
    //---

    public List<String> getAdminList(){return Config.getAdminList();}
    public boolean containsAdmin(String adm){return Config.containsAdmin(adm);}
    public int onlineAdmins(){
        return Config.getAdminsOnline();
    }

    public boolean enablePvP(){return Config.onPvP();}
    public boolean disablePvP(){return Config.offPvP();}
    //
    public void targetMsg(Player player, String msg){
        targetMsgNP(player, prefix+msg);
    }
    public void targetMsgNP(Player p, String msg){
        U.targetMessageNP(p, msg);
    }
    public Component mensajeConPrefix(String msg){
        return mensajeConColor(prefix+msg);
    }
    public Component mensajeConColor(String msg){
        return U.mensajeConColor(msg);
    }
    public void mensajeConsola(String msg){
        mensajeConsolaNP(prefix+msg);
    }
    public void mensajeConsolaNP(String msg){
        U.mensajeConsolaNP(msg);
    }
    public String componentToString(Component component){
        return U.componentToStringMsg(component);
    }
    public Component componentFromString(String msg){
        return U.componentColor(msg);
    }
    public void mensajeForAllNP(String msg){
        for(Player player : Bukkit.getOnlinePlayers()){
            targetMsgNP(player, msg);
        }
    }
    public void sendTitleTarget(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut){
        U.sendTitleTarget(p, title, subtitle, fadeIn, stay, fadeOut);
    }
    public void sendTitleToAll(String title, String subtitle, int fadeIn, int stay, int fadeOut){
        U.sendTitleToAll(title, subtitle, fadeIn, stay, fadeOut);
    }
    public void playSoundTarget(Player p, Sound sound, float vol, float pitch){
        U.playsoundTarget(p, sound, vol, pitch);
    }
    public void playSoundToAll(Sound sound, float vol, float pitch){
        U.playsoundForAll(sound, vol, pitch);
    }

    public void staticActionBar(Player p, String msg){
        U.staticActionBar(p, msg);
    }
    public void staticActionBarForAll(String msg){
        U.staticActionBarForAll(msg);
    }
    public void stopActionBar(UUID uuid){
        U.stopStaticActionBar(uuid);
    }
    public void stopActionBarForAll(){
        U.stopStaticActionBarForAll();
    }

    public double truncar(double value, int decimales){
        return U.truncar(value, decimales);
    }
    public int randomInt(int min, int max){
        return U.getRandomInt(min, max);
    }
    public double randomDouble(double min, double max){
        return U.getRandomDouble(min, max);
    }
    public String getLastVersionGithub(String urlGithub){
        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlGithub))
                    .header("User-Agent", "DottUtils-Version-Checker")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String body = response.body();

                int start = body.indexOf("\"name\":\"") + 8;
                int end = body.indexOf("\"", start);
                return body.substring(start, end);
            } else {
                mensajeConsola("&cError al obtener versión: " + response.statusCode());
                return null;
            }

        }catch(Exception e){
            mensajeConsola("&cOcurrió un problema intentando conseguir la última versión de github. Details:");
            mensajeConsolaNP("c"+ Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    public void startCountdownForAll(int segundos, String format){
        U.countdownForAll(plugin, segundos, format);
    }
    public void startCountdownTarget(Player p, int s, String format){
        U.countdownForTarget(p, plugin, s, format);
    }

    public void stopCountdownTarget(UUID uuid){
        U.stopCountdownTarget(uuid);
    }
    public void stopCountdowns(){
        U.stopAllCountdowns();
    }

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

    public void blackScreen(Player p, boolean forceIt){
        U.blackScreen(plugin, p, forceIt);
    }
    public void blackScreenForAll(boolean forceIt){
        U.blackScreenForAll(plugin, forceIt);
    }
    public void stopBlackScreen(Player p){
        U.stopBlackScreen(p);
    }
    public void stopBlackScreenForAll(){
        U.stopBlackScreenForAll();
    }

    //
    public String getPrefix(){return prefix;}
}
