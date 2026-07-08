package mp.dottiewh.api;

import mp.dottiewh.DottUtils;
import mp.dottiewh.api.directors.DuCinematicsDirector;
import mp.dottiewh.api.directors.DuItemsDirector;
import mp.dottiewh.api.directors.DuMusicsDirector;
import mp.dottiewh.cinematics.CinematicsConfig;
import mp.dottiewh.config.Config;
import mp.dottiewh.config.ConfigYmlReader;
import mp.dottiewh.config.CustomConfig;
import mp.dottiewh.music.MusicConfig;
import mp.dottiewh.utils.U;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DuApiManager {
    org.bukkit.plugin.Plugin plugin;
    String prefix;
    String debugPrefix;
    private boolean debugMode = false;

    public DuApiManager(org.bukkit.plugin.Plugin plugin, String prefix) {
        this.plugin = plugin;
        this.prefix = prefix;
        this.debugPrefix = "&c[DEBUG] " + prefix;

        U.mensajeConsolaNP(prefix + "se ha suscrito correctamente a la api de " + DottUtils.prefix);
    }
    //---

    public Plugin getDottUtilsPlugin() {
        return DottUtils.getPlugin();
    }

    public List<String> getAdminList() {
        return Config.getAdminList();
    }

    public boolean containsAdmin(String adm) {
        return Config.containsAdmin(adm);
    }

    public int onlineAdmins() {
        return Config.getAdminsOnline();
    }

    public boolean enablePvP() {
        return Config.onPvP();
    }

    public boolean disablePvP() {
        return Config.offPvP();
    }

    public boolean isPvPenabled() {
        return Config.getPvPStatus();
    }

    //
    public void targetMsg(Player player, String msg) {
        U.targetMessageNP(player, prefix + msg);
    }

    public void targetMsgNP(Player p, String msg) {
        U.targetMessageNP(p, msg);
    }
    public void targetMsg(CommandSender sender, String msg) {
        U.targetMessageNP(sender, prefix + msg);
    }

    public void targetMsgNP(CommandSender sender, String msg) {
        U.targetMessageNP(sender, msg);
    }

    public Component mensajeConPrefix(String msg) {
        return mensajeConColor(prefix + msg);
    }

    public Component mensajeConColor(String msg) {
        return U.mensajeConColor(msg);
    }

    public void mensajeConsola(String msg) {
        mensajeConsolaNP(prefix + msg);
    }

    public void mensajeConsolaNP(String msg) {
        U.mensajeConsolaNP(msg);
    }

    public String componentToString(Component component) {
        return U.componentToStringMsg(component);
    }
    public Component componentFromStringMiniHex(String msg){
        return U.componentColorHexMini(msg);
    }
    public Component componentFromString(String msg) {
        return U.componentColor(msg);
    }

    public void mensajeForAllNP(String msg) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            targetMsgNP(player, msg);
        }
    }

    public void debugMsg(String msg, CommandSender sender) {
        if(debugMode) U.targetMessageNP(sender, debugPrefix+msg);
    }
    public void consoleDebugMsg(String msg){
        if(debugMode) U.mensajeConsolaNP(debugPrefix+msg);
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

    public void actionBar(Player p, String msg){
       U.actionBar(p, msg);
    }
    public void actionBarForAll(String msg){
        U.actionBarForAll(msg);
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

    public void addPotionEffect(PotionEffectType effect, Player p, int duration, int amplifier, boolean ambient, boolean particles, boolean icon){
        U.addPotionEffect(effect, p, duration, amplifier, ambient, particles, icon);
    }
    public void addPotionEffectForAll(PotionEffectType effect, int duration, int amplifier, boolean ambient, boolean particles, boolean icon){
        U.addPotionEffectForAll(effect, duration, amplifier, ambient, particles, icon);
    }
    public void removePotionEffectForAll(PotionEffectType effect){
        for(Player p : Bukkit.getOnlinePlayers()){
            p.removePotionEffect(effect);
        }
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
    public boolean isPar(int n){
        return U.isPar(n);
    }
    public boolean isPar(long n){
        return U.isPar(n);
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


    public DuMusicsDirector getMusicDirector(){
        return new DuMusicsDirector();
    }
    /** Use DuMusicsDirector with getMusicDirector() method instead
     */
    @Deprecated(since = "1.2.3")
    public void playSong(String song, Player player, boolean loop){
        MusicConfig.reproduceTo(song, player, loop);
    }
    /** Use DuMusicsDirector with getMusicDirector() method instead
     */
    @Deprecated(since = "1.2.3")
    public void playSongForAll(String song, boolean loop){
        MusicConfig.reproduceToAll(song, loop);
    }
    /** Use DuMusicsDirector with getMusicDirector() method instead
     */
    @Deprecated(since = "1.2.3")
    public void stopSong(){
        MusicConfig.stopMusicTasks();
    }
    /** Use DuMusicsDirector with getMusicDirector() method instead
     */
    @Deprecated(since = "1.2.3")
    public void stopSong(UUID uuid){
        MusicConfig.stopMusicTasks(uuid);
    }
    //======================================
    public DuCinematicsDirector getCinematicsDirector(){
        return new DuCinematicsDirector();
    }
    /** Use DuCinematicsDirector with getCinematicsDirector() method instead
     */
    @Deprecated(since = "1.2.3")
    public void playCinematic(Player p, String fileName, boolean clonePlayer){
        CinematicsConfig.reproduceCinematic(p, fileName, clonePlayer, true);
    }
    /** Use DuCinematicsDirector with getCinematicsDirector() method instead
     */
    @Deprecated(since = "1.2.3")
    public void playCinematicForAll(String fileName, boolean clonePlayer){
        CinematicsConfig.reproduceCinematicForAll(fileName, clonePlayer);
    }
    /** Use DuCinematicsDirector with getCinematicsDirector() method instead
     */
    @Deprecated(since = "1.2.3")
    public void stopCinematic(UUID uuid){
        CinematicsConfig.stopReproducing(uuid);
    }
    /** Use DuCinematicsDirector with getCinematicsDirector() method instead
     */
    @Deprecated(since = "1.2.3")
    public void stopCinematicForAll(){
        CinematicsConfig.stopReproducingForAll();
    }

    public DuItemsDirector getItemsDirector(){
        return new DuItemsDirector();
    }

    public void blackScreen(Player p, boolean forceIt){
        U.blackScreen(plugin, p, forceIt);
    }
    public void blackScreenForAll(boolean forceIt){
        U.blackScreenForAll(plugin, forceIt);
    }
    public void blackScreen(Player p, boolean forceIt, int time){
        U.blackScreen(plugin, p, forceIt, time);
    }
    public void blackScreenForAll(boolean forceIt, int time){
        U.blackScreenForAll(plugin, forceIt, time);
    }
    public void stopBlackScreen(Player p){
        U.stopBlackScreen(p);
    }
    public void stopBlackScreenForAll(){
        U.stopBlackScreenForAll();
    }
    //

    /**
     * Remember to do cConfig.registerConfig(); later!
     */
    public CustomConfig createCustomConfig(@NotNull String fileName, @Nullable String folderName, boolean newFile){
        return new CustomConfig(fileName, folderName, plugin, newFile);
    }
    public ConfigYmlReader createConfigYmlReader(CustomConfig config){
        return new ConfigYmlReader(config, prefix);
    }
    //
    public String getPrefix(){return prefix;}

    public String getDebugPrefix() {
        return debugPrefix;
    }

    public void setDebugPrefix(String debugPrefix) {
        this.debugPrefix = debugPrefix;
    }

    public boolean isDebugMode() {
        return debugMode;
    }
    public DuApiManager setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }
}
