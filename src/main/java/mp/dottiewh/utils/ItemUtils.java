package mp.dottiewh.utils;

import com.destroystokyo.paper.ParticleBuilder;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import mp.dottiewh.DottUtils;
import mp.dottiewh.items.ItemConfig;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ItemUtils {
    private static final Plugin plugin = DottUtils.getPlugin();
    private static final String pluginKey = plugin.getName().toLowerCase();

    public static EquipmentSlotGroup getSlotFromString(String slot) {
        switch (slot.toUpperCase()){
            case "ARMOR" -> {
                return EquipmentSlotGroup.ARMOR;
            }
            case "BODY"->{
                return EquipmentSlotGroup.BODY;
            }
            case "CHEST"->{
                return EquipmentSlotGroup.CHEST;
            }
            case "FEET"->{
                return EquipmentSlotGroup.FEET;
            }
            case "HAND"->{
                return EquipmentSlotGroup.HAND;
            }
            case "HEAD"->{
                return EquipmentSlotGroup.HEAD;
            }
            case "LEGS"->{
                return EquipmentSlotGroup.LEGS;
            }
            case "MAINHAND"->{
                return EquipmentSlotGroup.MAINHAND;
            }
            case "OFFHAND"->{
                return EquipmentSlotGroup.OFFHAND;
            }
            case "SADDLE"->{
                return EquipmentSlotGroup.SADDLE;
            }
            case "ANY"->{
                return EquipmentSlotGroup.ANY;
            }
            //-------------------------
            default-> {
                return EquipmentSlotGroup.ANY;
            }
        }
    }
    public static String consumeEffectToString(ConsumeEffect effect){
        if (effect instanceof ConsumeEffect.ApplyStatusEffects) return "ApplyStatusEffects";
        if (effect instanceof ConsumeEffect.ClearAllStatusEffects) return "ClearAllStatusEffects";
        if (effect instanceof ConsumeEffect.PlaySound) return "PlaySound";
        if (effect instanceof ConsumeEffect.RemoveStatusEffects) return "RemoveStatusEffects";
        if (effect instanceof ConsumeEffect.TeleportRandomly) return "TeleportRandomly";
        return "null";
    }

    public static Consumable consumableBuilderU(ConfigurationSection consumableSection, String name){
        float consumeSeconds = (float) consumableSection.getDouble("seconds");//CONSUMABLETIME
        // ANIMACIÓN
        String sAnimation = consumableSection.getString("animation");
        ItemUseAnimation realAnimation;
        if (sAnimation!=null){
            try{
                realAnimation = ItemUseAnimation.valueOf(sAnimation);
            }catch (Exception e){
                realAnimation = ItemUseAnimation.EAT;
                U.mensajeConsolaNP("Problema intentando cargar animación de consumible "+sAnimation+" | "+name);
            }
        }else realAnimation = ItemUseAnimation.EAT;
        //SONIDO
        String sSound = consumableSection.getString("sound");
        Key realSound;
        if (sSound!=null){
            try{
                sSound = sSound.toLowerCase();
                realSound = Key.key("minecraft:"+sSound);
            }catch (Exception e){
                realSound = Key.key("minecraft:entity.generic.eat");
                U.mensajeConsolaNP("Problema intentando cargar animación de consumible "+sSound+" | "+name);
            }
        }else realSound = Key.key("minecraft:entity.generic.eat");
        //PARTICLES
        boolean bConsumeParticles = consumableSection.getBoolean("consumeparticles");
        //---------------EXTRAS---------
        List<ConsumeEffect> effectsList = new ArrayList<>();

        //ClearAllStatusEffects
        ConfigurationSection clearAllEffectSection = consumableSection.getConfigurationSection("ClearAllStatusEffects");
        if (clearAllEffectSection!=null){
            boolean caeStatus = clearAllEffectSection.getBoolean("status");
            if (caeStatus){
                ConsumeEffect toAdd = ConsumeEffect.clearAllStatusEffects();
                effectsList.add(toAdd);
            }
        }
        //RemoveStatusEffects
        ConfigurationSection remEffectSection = consumableSection.getConfigurationSection("RemoveStatusEffects");
        if (remEffectSection!=null){
            boolean allRight = false;
            List<String> listaEfectos = remEffectSection.getStringList("effects");
            List<TypedKey<PotionEffectType>> typedKeys = new ArrayList<>();

            for(String eff : listaEfectos){
                try{
                    if (eff.isEmpty()) continue;
                    String totalPath = "minecraft:" + eff.toLowerCase();

                    NamespacedKey namespacedKey = NamespacedKey.fromString(totalPath);

                    RegistryAccess regAccess = RegistryAccess.registryAccess();
                    Registry<PotionEffectType> registry = regAccess.getRegistry(RegistryKey.MOB_EFFECT);
                    assert namespacedKey != null;
                    PotionEffectType efToAdd = registry.get(namespacedKey);

                    Key key = Key.key(totalPath);
                    TypedKey<PotionEffectType> typedKey = TypedKey.create(RegistryKey.MOB_EFFECT, key);

                    typedKeys.add(typedKey);
                    allRight =true;
                }catch (Exception e){
                    U.mensajeConsolaNP("&cProblema con efecto de poción en consumible. Efecto: "+eff+" | "+name);
                    U.mensajeConsolaNP("&c"+Arrays.toString(e.getStackTrace()));
                    U.mensajeConsolaNP("minecraft:"+eff);
                    continue;
                }
            }
            if (allRight){
                TypedKey<PotionEffectType>[] toAdd = typedKeys.toArray(new TypedKey[0]);
                RegistryKeySet registryKeySet = RegistrySet.keySet(RegistryKey.MOB_EFFECT, toAdd);
                effectsList.add(ConsumeEffect.removeEffects(registryKeySet));
            }
        }
        //ConsumeEffect.ApplyStatusEffects
        ConfigurationSection applyEffSection = consumableSection.getConfigurationSection("ApplyStatusEffects");
        if (applyEffSection!=null){
            float prob = (float) applyEffSection.getDouble("probability"); // 0-1
            List<PotionEffect> listEfectos = new ArrayList<>();

            for(String efectito : applyEffSection.getKeys(false)){
                if (efectito.equalsIgnoreCase("probability")) continue;
                int duration=0, amplifier=0;

                String totalPath = "minecraft:" + efectito.toLowerCase();
                NamespacedKey namespacedKey = NamespacedKey.fromString(totalPath);

                PotionEffectType effectType = getPotionFromString(efectito);

                ConfigurationSection valuesSection = applyEffSection.getConfigurationSection(efectito);
                if (valuesSection!=null){
                     duration = valuesSection.getInt("duration");
                     amplifier = valuesSection.getInt("amplifier");
                }else{
                    U.mensajeConsolaNP("&cEu, revisate el efecto de poción "+efectito+", en el item "+name+" en tu yml.");
                    continue;
                }
                PotionEffect toAdd = new PotionEffect(effectType, duration, amplifier);
                listEfectos.add(toAdd);
            }
            ConsumeEffect fToAdd = ConsumeEffect.applyStatusEffects(listEfectos, prob);
            effectsList.add(fToAdd);
        }
        //TeleportRandomly
        ConfigurationSection tpRandomSection = consumableSection.getConfigurationSection("TeleportRandomly");
        if (tpRandomSection!=null){
            float diametro = (float) tpRandomSection.getDouble("diameter");
            effectsList.add(ConsumeEffect.teleportRandomlyEffect(diametro));
        }
        //PlaySound
        ConfigurationSection playSoundSection = consumableSection.getConfigurationSection("PlaySound");
        if (playSoundSection!=null){
            String soundS = playSoundSection.getString("sound");
            try{
                String totalPath = "minecraft:" + soundS.toLowerCase();
                Key key = Key.key(totalPath);
                ConsumeEffect pToAdd = ConsumeEffect.playSoundConsumeEffect(key);
                effectsList.add(pToAdd);
            }catch(Exception e){
                U.mensajeConsolaNP("Prueba a revisar tu sonido que tienes en "+name+", está como "+soundS);
                U.mensajeConsolaNP("&c"+Arrays.toString(e.getStackTrace()));
            }
        }

        // Builder
        Consumable consumable = Consumable.consumable()//start
                .consumeSeconds(consumeSeconds)
                .animation(realAnimation)
                .sound(realSound)
                .hasConsumeParticles(bConsumeParticles)
                .addEffects(effectsList)
                .build(); //end

        return consumable;
    }

    public static PotionEffectType getPotionFromString(String effectS){
        String totalPath = "minecraft:" + effectS.toLowerCase();
        NamespacedKey namespacedKey = NamespacedKey.fromString(totalPath);

        RegistryAccess regAccess = RegistryAccess.registryAccess();
        Registry<PotionEffectType> registry = regAccess.getRegistry(RegistryKey.MOB_EFFECT);
        assert namespacedKey != null;

        return registry.get(namespacedKey);
    }

    @NotNull
    public static ItemMeta addPersistentDataString(@NotNull ItemMeta meta, @NotNull String key, String value){
        PersistentDataContainer persistentDataContainer = meta.getPersistentDataContainer();
        NamespacedKey nKey = new NamespacedKey(plugin, key);
        persistentDataContainer.set(nKey, PersistentDataType.STRING, value);
        return meta;
    }
    @Nullable
    public static String getPersistentDataString(@NotNull ItemMeta meta, @NotNull String key){
        PersistentDataContainer persistentDataContainer = meta.getPersistentDataContainer();
        NamespacedKey nKey = new NamespacedKey(plugin, key);
        return persistentDataContainer.get(nKey, PersistentDataType.STRING);
    }
    @NotNull
    public static String getPersistentDataString(@NotNull ItemMeta meta, @NotNull String key, @NotNull String def){
        String output = getPersistentDataString(meta, key);
        if(output==null) return def;
        return output;
    }

    //=========================
    public static void checkItemAttackCustomData(EntityDamageByEntityEvent event){
        if(event.isCancelled()) return;
        if(!(event.getDamager() instanceof HumanEntity damager)) return;

        ItemStack mainItem = damager.getInventory().getItemInMainHand();
        ItemMeta meta = mainItem.getItemMeta();
        if(meta==null) return;
        if(!containsKey(meta)) return;

        Entity victim = event.getEntity();
        Location loc = victim.getLocation();

        ParticleBuilder particleData = ItemConfig.loadParticleData(meta, "onAttack", loc.clone().add(0, 1.6, 0).add(victim.getVelocity()));
        PotionEffect effectData = ItemConfig.loadPotionEffect(meta, "onAttack");

        if(particleData!=null){
            if(particleData.particle().equals(Particle.TRAIL)) particleData.location(damager.getLocation());
            else particleData.location(loc);

            particleData.spawn();
        }
        if(effectData!=null){
            if(!(victim instanceof LivingEntity victimLiving)) return;
            victimLiving.addPotionEffect(effectData);
        }
    }
    public static void checkItemDeathCustomData(EntityDeathEvent event){
        if(event.isCancelled()) return;
        Entity source = event.getDamageSource().getCausingEntity();
        if(source==null) return;
        if(!(source instanceof HumanEntity humanSource)) return;
        ItemStack mainItem = humanSource.getInventory().getItemInMainHand();
        if(!mainItem.hasItemMeta()) return;

        ItemMeta meta = mainItem.getItemMeta();
        if(!containsKey(meta)) return;

        Location loc = event.getEntity().getEyeLocation();
        ParticleBuilder particleData = ItemConfig.loadParticleData(meta, "onKill", loc.clone().add(0, 5, 0));
        if(particleData!=null){
            particleData.location(loc);
            particleData.spawn();
        }
    }

    //
    private static boolean containsKey(ItemMeta meta){
        //U.mensajeDebugConsole(pluginKey);
        Set<NamespacedKey> set = meta.getPersistentDataContainer().getKeys();
        List<String> listKeys = new LinkedList<>();
        set.forEach(key-> {
            listKeys.add(key.getNamespace());
            //U.mensajeDebugConsole(key.toString()+" | "+key.getNamespace());
        });
        boolean status = listKeys.contains(pluginKey);
        //U.mensajeDebugConsole(""+status);
        return status;
    }
}
