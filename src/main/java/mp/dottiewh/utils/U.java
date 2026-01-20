package mp.dottiewh.utils;

import mp.dottiewh.DottUtils;
import mp.dottiewh.config.Config;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import static mp.dottiewh.DottUtils.getPlugin;
import static mp.dottiewh.DottUtils.prefix;

public class U { //Stands for utils
    private static final Set<UUID> listaNoFall = new HashSet<>();
    private static final String urlGithub = "https://api.github.com/repos/Dottiewh/DottUtils/releases/latest";
   // private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final Map<UUID, List<BukkitRunnable>> mapaCountdowns = new HashMap<>();

    private static final Map<UUID, BukkitRunnable> mapaRepetitivoScreen = new HashMap<>();
    private static final Map<UUID, BukkitRunnable> mapaRepetitivoActionBar = new HashMap<>();
    private static final Map<UUID, List<ItemDisplay>> mapaEntityScreen = new HashMap<>();

    //--------------------------Métodos Útiles-----------------------------------
    public static void targetMessage(Player target, String mensaje){
        target.sendMessage(U.mensajeConPrefix(mensaje));
    }
    public static void targetMessageNP(Player target, String mensaje){
        target.sendMessage(U.mensajeConColor(mensaje));
    }
    public static void targetMessageNP(CommandSender target, String mensaje){
        target.sendMessage(U.mensajeConColor(mensaje));
    }
    // all in ticks
    public static void sendTitleTarget(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut){
        Component cTitle = componentColor(title);
        if(subtitle==null) subtitle="";
        Component cSubTitle = componentColor(subtitle);

        Title titulo = Title.title(cTitle, cSubTitle, fadeIn, stay, fadeOut);
        p.showTitle(titulo);
    }
    public static void sendTitleToAll(String title, String subtitle, int fadeIn, int stay, int fadeOut){
        for(Player player : Bukkit.getOnlinePlayers()){
            sendTitleTarget(player, title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public static void playsoundTarget(Player p, Sound sound, float vol, float pitch){
        p.playSound(p, sound, vol, pitch);
    }
    public static void playsoundForAll(Sound sound, float vol, float pitch){
        for(Player player : Bukkit.getOnlinePlayers()){
            playsoundTarget(player, sound, vol, pitch);
        }
    }

    // INCLUYENDO MIN Y MAX
    public static int getRandomInt(int min, int max){
        Random random = new Random();
        return random.nextInt((max-min)+1)+min;
    }
    // NO INCLUYE MAX, se queda en max-0,00...001
    public static double getRandomDouble(double min, double max){
        Random random = new Random();
        return random.nextDouble(max-min)+min;
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

    public static void mensajeDebug(String mensaje, CommandSender sender){
        if(!Config.getBoolean("debug_mode", false)) return;
        String prefix = U.getMsgPath("debug_prefix", "&8&l[&4&lDU &c&Debug&8&l] ");
        targetMessageNP(sender, prefix+mensaje);
    }
    public static void mensajeDebugConsole(String mensaje){
        mensajeDebug(mensaje, Bukkit.getConsoleSender());
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

    public static void hidePlayerForAll(Player p){
        for(Player pOthers : Bukkit.getOnlinePlayers()){
            pOthers.hidePlayer(DottUtils.getPlugin(), p);
        }
    }
    public static void unHidePlayerForAll(Player p){
        for(Player pOthers : Bukkit.getOnlinePlayers()){
            pOthers.showPlayer(DottUtils.getPlugin(), p);
        }
    }

    //util long methods
    public static boolean isPar(long n){
        return (n%2 == 0);
    }
    public static boolean isPar(int n){
        return (n%2 == 0);
    }

    public static void addPotionEffect(PotionEffectType effect, Player p, int ticks, int amplifier, boolean ambient, boolean particles, boolean icon){
        PotionEffect potionEffect = new PotionEffect(effect, ticks, amplifier, ambient, particles, icon);
        p.addPotionEffect(potionEffect);
    }
    public static void addPotionEffectForAll(PotionEffectType effect, int ticks, int amplifier, boolean ambient, boolean particles, boolean icon){
        PotionEffect potionEffect = new PotionEffect(effect, ticks, amplifier, ambient, particles, icon);
        for(Player p : Bukkit.getOnlinePlayers()){
            p.addPotionEffect(potionEffect);
        }
    }

    public static void actionBarForAll(String msg){
        for(Player p : Bukkit.getOnlinePlayers()){
            actionBar(p, msg);
        }
    }
    public static void actionBar(Player p, String msg){
        p.sendActionBar(componentColor(msg));
    }
    public static void staticActionBar(Player p, String msg){
        UUID uuid = p.getUniqueId();
        stopStaticActionBar(uuid);
        stopCountdownTarget(uuid);

        Component cMsg = componentColor(msg);
        BukkitRunnable repetitive = new BukkitRunnable() {
            @Override
            public void run() {
                p.sendActionBar(cMsg);
            }
        };
        repetitive.runTaskTimer(DottUtils.getPlugin(), 0L, 20L);
        mapaRepetitivoActionBar.put(uuid, repetitive);
    }
    public static void staticActionBarForAll(String msg){
        for(Player p : Bukkit.getOnlinePlayers()){
            staticActionBar(p, msg);
        }
    }

    public static void stopStaticActionBarForAll(){
        for(Player p : Bukkit.getOnlinePlayers()){
            stopStaticActionBar(p.getUniqueId());
        }
    }
    public static void stopStaticActionBar(UUID uuid){
        if(!(mapaRepetitivoActionBar.containsKey(uuid))) return;
        BukkitRunnable runnable = mapaRepetitivoActionBar.remove(uuid);
        runnable.cancel();
    }

    public static void countdownForAll(Plugin pl, int segundos, String format){ // Segundos restantes: 77
        stopAllCountdowns();
        for(Player p : Bukkit.getOnlinePlayers()){
            countdownForTarget(p, pl, segundos, format);
        }
    }
    public static void countdownForTarget(Player p, Plugin pl, int segundos, String format){ // Segundos restantes: 77
        UUID uuid = p.getUniqueId();
        stopCountdownTarget(uuid);
        stopStaticActionBar(uuid);

        List<BukkitRunnable> listaCountdowns = new LinkedList<>();

        int segundosRestantes = segundos;
        for(int i=0;i<segundos;i++){
            int finalSegundosRestantes = segundosRestantes;

            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run(){
                    Component msg = componentColor(format+ finalSegundosRestantes); //+" &8(s)"
                    p.sendActionBar(msg);
                }
            };
            listaCountdowns.add(task);
            task.runTaskLater(pl, i*20L);

            segundosRestantes--;
        }
        // final
        BukkitRunnable taskEnd = new BukkitRunnable() {
            @Override
            public void run(){
                Component msg = componentColor("&cCuenta atrás acabada.");

                p.sendActionBar(msg);
                stopCountdownTarget(uuid);
            }
        };
        listaCountdowns.add(taskEnd);
        taskEnd.runTaskLater(pl, segundos*20L);
        //
        mapaCountdowns.put(uuid, listaCountdowns);
    }

    public static void stopCountdownTarget(UUID uuid){
        if(!(mapaCountdowns.containsKey(uuid))) return;

        //List<BukkitRunnable> runnables = mapaCountdowns.remove(uuid);
        Iterator<BukkitRunnable> it = mapaCountdowns.remove(uuid).iterator();

        while(it.hasNext()){
            BukkitRunnable runnable = it.next();
            runnable.cancel();
            it.remove();
        }
    }

    public static void stopAllCountdowns(){
        for(Player p : Bukkit.getOnlinePlayers()){
            stopCountdownTarget(p.getUniqueId());
        }
    }

    public static void blackScreenForAll(Plugin plugin, boolean forceIt){
        for(Player p : Bukkit.getOnlinePlayers()){
            blackScreen(plugin, p, forceIt);
        }
    }
    public static void blackScreenForAll(Plugin plugin, boolean forceIt, int time){
        for(Player p : Bukkit.getOnlinePlayers()){
            blackScreen(plugin, p, forceIt, time);
        }
    }
    public static void blackScreen(Plugin plugin, Player player, boolean forceIt){
        sendTitleTarget(player, "\uE123", null, 20, 9999999, 20);
        coreBlackScreen(plugin, player, forceIt);
    }
    public static void blackScreen(Plugin plugin, Player player, boolean forceIt, int time){
        time = time*20;
        sendTitleTarget(player, "\uE123", null, 20, time, 20);
        coreBlackScreen(plugin, player, forceIt);
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            stopBlackScreen(player);
        }, time);
    }
    //
    private static void coreBlackScreen(Plugin plugin, Player player, boolean forceIt){
        stopForceBlackScreen(player.getUniqueId()); //just in case
        addPotionEffect(PotionEffectType.DARKNESS, player, PotionEffect.INFINITE_DURATION, 0, true, false, false);
        if(forceIt){
            Bukkit.getScheduler().runTaskLater(plugin, task->{
                forceBlackOut(plugin, player);
            }, 20L);
        }
    }

    public static void stopBlackScreen(Player p){
        sendTitleTarget(p, "", null, 0, 5, 0);
        p.removePotionEffect(PotionEffectType.DARKNESS);
        stopForceBlackScreen(p.getUniqueId()); // just to be sure
        AttributeInstance attr = p.getAttribute(Attribute.CAMERA_DISTANCE);
        if(attr==null) return;
        if(attr.getValue()!=1) return;
        attr.setBaseValue(4);
    }
    public static void stopBlackScreenForAll(){
        for(Player p : Bukkit.getOnlinePlayers()){
            stopBlackScreen(p);
        }
    }


    private static void forceBlackOut(Plugin plugin, Player player){
        AttributeInstance attr = player.getAttribute(Attribute.CAMERA_DISTANCE);
        if(attr!=null){
            attr.setBaseValue(1);
        }

        List<ItemDisplay> displays = new ArrayList<>();

        World world = player.getWorld();
        Location base = player.getEyeLocation();

        float scale =4f;
        for (int i = 0; i < 6; i++) {
            float sX=scale, sY=scale, sZ=scale;

            ItemDisplay iD = world.spawn(base, ItemDisplay.class, t -> {

            });
            Transformation old = iD.getTransformation();

            switch(i){
                // X
                case 0, 1->{
                    sX=0.1f;
                } //Y
                case 2, 3->{
                    sY=0.1f;
                }// Z
                default->{
                    sZ=0.1f;
                }
            }

            Transformation modified = new Transformation(
                    old.getTranslation(),
                    old.getLeftRotation(),
                    new Vector3f(sX, sY, sZ),
                    old.getRightRotation()
            );

            iD.setTransformation(modified);


            iD.setItemStack(ItemStack.of(Material.BLACK_CONCRETE));
            iD.setVisibleByDefault(false);

            iD.setInterpolationDuration(3);
            iD.setTeleportDuration(1);

            player.showEntity(plugin, iD);
            displays.add(iD);
        }
        mapaEntityScreen.put(player.getUniqueId(), displays);

        // Distancia desde la cámara
        double d = 1.1;
        double dF = 1.9;

        BukkitRunnable repetitive = new BukkitRunnable() {
            @Override
            public void run() {
                Location pLoc = player.getLocation().add(0, 1.4, 0);
                pLoc.setYaw(0f); pLoc.setPitch(0f);

                Location x = pLoc.clone().add(dF, 0, 0);
                Location y = pLoc.clone().add(0, d, 0);
                Location z = pLoc.clone().add(0, 0, dF);
                //
                Location x2 = pLoc.clone().subtract(dF, 0, 0);
                Location y2 = pLoc.clone().subtract(0, d, 0);
                Location z2 = pLoc.clone().subtract(0, 0, dF);

                displays.get(0).teleport(x);
                displays.get(1).teleport(x2);
                displays.get(2).teleport(y);
                displays.get(3).teleport(y2);
                displays.get(4).teleport(z);
                displays.get(5).teleport(z2);
            }
        };
        mapaRepetitivoScreen.put(player.getUniqueId(), repetitive);
        repetitive.runTaskTimer(plugin, 1L, 1L);
    }
    public static void stopForceBlackScreen(UUID uuid){
        if(!(mapaRepetitivoScreen.containsKey(uuid))) return;
        mapaRepetitivoScreen.get(uuid).cancel();
        mapaRepetitivoScreen.remove(uuid);

        //remove of entitys
        List<ItemDisplay> displays = mapaEntityScreen.remove(uuid);
        if (displays != null) {
            for (ItemDisplay item : displays) {
                if (!item.isDead()) {
                    item.remove();
                }
            }
        }
    }

    public static void cleanOnDisable(){
        for (UUID uuid : mapaEntityScreen.keySet()) {
            stopForceBlackScreen(uuid);
        }
    }
}
