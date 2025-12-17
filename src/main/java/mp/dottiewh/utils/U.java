package mp.dottiewh.utils;

import mp.dottiewh.DottUtils;
import mp.dottiewh.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import static mp.dottiewh.DottUtils.prefix;

public class U { //Stands for utils
    private static final Set<UUID> listaNoFall = new HashSet<>();
    private static final String urlGithub = "https://api.github.com/repos/Dottiewh/DottUtils/releases/latest";
   // private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final Set<BukkitRunnable> listaCountdowns = new HashSet<>();


    //--------------------------Métodos Útiles-----------------------------------
    public static void targetMessage(Player target, String mensaje){
        target.sendMessage(U.mensajeConPrefix(mensaje));
    }
    public static void targetMessageNP(Player target, String mensaje){
        target.sendMessage(U.mensajeConColor(mensaje));
    }
    public static Component mensajeConPrefix(String mensaje){
        return componentColor(prefix+mensaje);
    }
    public static Component mensajeConColor(String mensaje){
        return componentColor(mensaje);
    }
    public static void mensajeConsola(String mensaje){
        Bukkit.getConsoleSender().sendMessage(componentColor(prefix+mensaje));
    }
    public static void mensajeConsolaNP(String mensaje){
        Bukkit.getConsoleSender().sendMessage(componentColor(mensaje));
    }

    public static String componentToStringMsg(Component component){
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }
    public static Component componentColor(String mensaje){
        return LegacyComponentSerializer.legacy('&').deserialize(mensaje);
    }
    public static void showAllStatus(){
        boolean noFallS = Config.getNoFallStatus(), wlS = Config.getWhiteListStatus();
        boolean pvpS = Config.getPvPStatus();

        String displayNoFallS = (noFallS) ? "&eHay daño de caída &cDESACTIVADO&e." : "&eHay daño de caída &aACTIVADO&e.";
        String displayWlS = (wlS) ? "&eLa whitelist está &aACTIVADA&e." : "&eLa whitelist está &cDESACTIVADA&e.";
        String displaypvpS = (pvpS) ? "&eEl pvp está &aACTIVADO&e." : "&eEl pvp está &cDESACTIVADO&e.";

        mensajeConsolaNP(displayNoFallS);
        mensajeConsolaNP(displayWlS);
        mensajeConsolaNP(displaypvpS);

    }
    //---------------no fall management---------
    public static void noFall_add(Player player){
        UUID uuid = player.getUniqueId();
        listaNoFall.add(uuid);
    }
    public static void noFall_remove(Player player){
        UUID uuid = player.getUniqueId();
        listaNoFall.remove(uuid);
    }
    public static boolean noFall_check(Player player){
        UUID uuid = player.getUniqueId();
        return listaNoFall.contains(uuid);
    }
    public static void noFall_core(EntityDamageEvent event){
        if (!(event.getEntity() instanceof Player player)) return;
        if (noFall_check(player)){
            event.setCancelled(true);
            noFall_remove(player);
        }
    }
    //-------------other-------------------
    public static void noPvP(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return; //victim
        if (!(event.getDamager() instanceof Player damager)) return; //damager

        if (Config.getPvPStatus()) return; // si pvp en true, se devuelve

        if(Config.getBoolean("pvp_bypass", false)){
            String dmgerName = damager.getName();
            if(Config.containsAdmin(dmgerName)) return;
        }

        event.setCancelled(true);
    }
    public static void noFall(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!Config.getNoFallStatus()) return; //si esta en false el no fall se devuelve

        if(!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) return;

        event.setCancelled(true);
    }
    public static String getMsgPath(String path){
        return DottUtils.ymlMessages.getConfig().getString(path,  null);
    }
    public static String getMsgPath(String path, String def){
        String toGive = DottUtils.ymlMessages.getConfig().getString(path, null);
        if (toGive==null){
            DottUtils.ymlMessages.getConfig().set(path, def);
            DottUtils.ymlMessages.saveConfig();
            U.mensajeConsola("&cNo se ha detectado el path &f"+path+"&c en messages.yml. Regenerando con "+def+"...");
            return def;
        }
        return toGive;
    }
    public static int getIntConfigPath (String path){
        return DottUtils.ymlConfig.getConfig().getInt(path);
    }
    public static double truncar(double value, int decimales){
        double factor = Math.pow(10, decimales);
        return Math.floor(value*factor) / factor;
    }
    public static int removeDecimals(double value){
        double dToGive = truncar(value,0);
        return (int) dToGive;
    }
    public static String getLastVersionGithub(){
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
            mensajeConsolaNP("c"+Arrays.toString(e.getStackTrace()));
            return null;
        }
    }
    //util long methods
    public static void countdownForAll(Plugin pl, int segundos, String format){ // Segundos restantes: 77
        stopAllCountdowns();

        int segundosRestantes = segundos;
        for(int i=0;i<segundos;i++){
            int finalSegundosRestantes = segundosRestantes;

            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run(){
                    Component msg = componentColor(format+ finalSegundosRestantes+" &8(s)");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendActionBar(msg);
                    }
                }
            };
            listaCountdowns.add(task);
            task.runTaskLater(pl, i*20L);

            segundosRestantes--;
        }
        // final
        int finalSegundosRestantes1 = segundosRestantes;
        BukkitRunnable taskEnd = new BukkitRunnable() {
            @Override
            public void run(){
                Component msg = componentColor("&cCuenta atrás acabada.");

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendActionBar(msg);
                }
                stopAllCountdowns();
            }
        };
        listaCountdowns.add(taskEnd);
        taskEnd.runTaskLater(pl, segundos*20L);
    }
    public static void stopAllCountdowns(){
        for(BukkitRunnable task : listaCountdowns){
            task.cancel();
        }
    }
}
