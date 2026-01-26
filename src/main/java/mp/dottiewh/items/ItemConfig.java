package mp.dottiewh.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import mp.dottiewh.DottUtils;
import mp.dottiewh.items.Exceptions.InvalidItemConfigException;
import mp.dottiewh.items.Exceptions.InvalidMaterialException;
import mp.dottiewh.items.Exceptions.ItemSectionEmpty;
import mp.dottiewh.items.Exceptions.MissingMaterialException;
import mp.dottiewh.utils.ItemUtils;
import mp.dottiewh.utils.U;
import mp.dottiewh.config.CustomConfig;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import io.papermc.paper.datacomponent.item.Consumable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class ItemConfig{
    //private static CustomConfig configMsg;
    private static CustomConfig configItem;
    private static Plugin plugin;
    //private static String prefix;



    public static void itemConfigInit(){
        //configMsg = DottUtils.getRegisteredMsgConfig();
        configItem = DottUtils.getRegisteredItemConfig();
        plugin=DottUtils.getPlugin();
        //prefix = U.getMsgPath("item_prefix");
    }

    public static void saveItem(@NotNull String name, @NotNull ItemStack item){ //path something like = Items.ItemName
        ConfigurationSection section = configItem.getConfig().createSection("Items."+name);

        section.set("Material", item.getType().name());

        if (item.hasItemMeta()){
            ItemMeta meta = item.getItemMeta();

            //--------------NAME----------------
            if (meta.hasDisplayName()) section.set("Name", U.componentToStringMsg(meta.displayName()));
            //-----------LORE-----------------------
            if (meta.hasLore()){
                List<Component> loreComp = meta.lore();
                if (loreComp!= null){
                    List<String> lorePlain = loreComp.stream().map(U::componentToStringMsg).toList(); //cast de comp a string
                    section.set("Lore", lorePlain);
                }
            }
            //----------ENCANTAMIENTOS------------
            if (meta.hasEnchants()){
                ConfigurationSection enchSection = section.createSection("Enchants");
                //guardar cada encantamiento
                meta.getEnchants().forEach((ench, lvl)->
                    enchSection.set(ench.getKey().getKey(), lvl));
            }
            //-------ATRIBUTOS-----------
            if (meta.hasAttributeModifiers()){
                Multimap<Attribute, AttributeModifier> modifiers = meta.getAttributeModifiers();
                if (modifiers!=null){
                    ConfigurationSection attrSection = section.createSection("Attributes");

                    for (Map.Entry<Attribute, AttributeModifier> entry : modifiers.entries()){
                        Attribute attribute = entry.getKey();

                        AttributeModifier mod = entry.getValue();


                        String path = attribute.getKey().getKey();
                        attrSection.set(path+".value", mod.getAmount());
                        attrSection.set(path+".operation", mod.getOperation().name());
                        attrSection.set(path+".slot", mod.getSlotGroup().toString());
                    }
                }
            }
            //-----COMIDA-------------
            if (meta.hasFood()){
                ConfigurationSection foodSection = section.createSection("Food");
                FoodComponent food = meta.getFood();
                boolean alwaysEat = food.canAlwaysEat();
                int nutrition = food.getNutrition();
                float saturation = food.getSaturation();


                foodSection.set("always_eatable", alwaysEat);
                foodSection.set("nutrition", nutrition);
                foodSection.set("saturation", saturation);
            }
            //---------UNBREAKABLE-------------
            if (meta.isUnbreakable()){
                section.set("Unbreakable", true);
            }
            //------MAXSTACKSIZE---------------
            if (meta.hasMaxStackSize()){
                int max = meta.getMaxStackSize();
                section.set("Max_stack_size", max);
            }
            //---------consumible---------
            var consumible = item.getData(DataComponentTypes.CONSUMABLE);
            if (consumible!=null){
                ConfigurationSection consumableSection = section.createSection("Consumable");
                consumableSection.set("seconds", consumible.consumeSeconds());

                if (consumible.animation()!=null){
                    ItemUseAnimation animation = consumible.animation();
                    consumableSection.set("animation", animation.name());
                }
                if (consumible.sound()!=null){
                    Key sound =  consumible.sound();
                    consumableSection.set("sound", sound.value().toUpperCase());
                }
                if (consumible.hasConsumeParticles()){
                    consumableSection.set("consumeparticles", true);
                }else consumableSection.set("consumeparticles", false);

                if (consumible.consumeEffects()!=null){
                    List<ConsumeEffect> efectos = consumible.consumeEffects();
                    for (ConsumeEffect effect : efectos){
                        String effectString = ItemUtils.consumeEffectToString(effect);
                        ConfigurationSection cEffectSection = consumableSection.createSection(effectString);
                        switch (effectString){
                            case "ApplyStatusEffects"->{
                                ConsumeEffect.ApplyStatusEffects applyStatus = (ConsumeEffect.ApplyStatusEffects) effect;
                                cEffectSection.set("probability", applyStatus.probability());
                                for (PotionEffect potion : applyStatus.effects()){

                                    NamespacedKey key = potion.getType().getKey();
                                    String stringPotionEffect = key.getKey().toUpperCase();
                                    ConfigurationSection potionSection = cEffectSection.createSection(stringPotionEffect);

                                    if (potion.isInfinite()){
                                        potionSection.set("duration", PotionEffect.INFINITE_DURATION);
                                    }
                                    else{
                                        potionSection.set("duration", potion.getDuration());
                                    }
                                    potionSection.set("amplifier", potion.getAmplifier());
                                }
                            }
                            case "ClearAllStatusEffects"->{
                                cEffectSection.set("status", true);
                            }
                            case "PlaySound"->{
                                ConsumeEffect.PlaySound cPlaySound = (ConsumeEffect.PlaySound) effect;

                                String stringSound = cPlaySound.sound().value().toUpperCase();
                                cEffectSection.set("sound", stringSound);
                            }
                            case "RemoveStatusEffects"->{
                                List<String> effectsList = new ArrayList<>();
                                ConsumeEffect.RemoveStatusEffects remEffect = (ConsumeEffect.RemoveStatusEffects) effect;
                                RegistryKeySet<PotionEffectType> toRemove = remEffect.removeEffects();

                                for (TypedKey<PotionEffectType> objetivo : toRemove){
                                    String sPotion = objetivo.key().value().toUpperCase();
                                    effectsList.add(sPotion);
                                }
                                cEffectSection.set("effects", effectsList);
                            }
                            case "TeleportRandomly"->{
                                ConsumeEffect.TeleportRandomly cTelRandom = (ConsumeEffect.TeleportRandomly) effect;

                                float diametro = cTelRandom.diameter();
                                cEffectSection.set("diameter", diametro);
                            }
                            //---
                            default->U.mensajeConsola("&cHa ocurrido un error intentando cargar un atributo de tipo "+effectString+" | "+name);
                        }

                    }
                }
            }
        }
        configItem.saveConfig();
    }

    @NotNull
    public static ItemStack loadItem(@NotNull String name){
        return loadItem(name, configItem);
    }

    @NotNull
    public static ItemStack loadItem(@NotNull String name, @NotNull CustomConfig iConfig){ //path something like = Items.ItemName
        boolean modifyData = false;
        Consumable consToAdd = null;
        
        String path = "Items."+name;
        ConfigurationSection section = iConfig.getConfig().getConfigurationSection(path);
        if (section==null){
            throw new InvalidItemConfigException(path, "Tu path no existe.");
        }

        // Material
        String matName = section.getString("Material"); //checkea si existe el path
        if (matName == null) throw new MissingMaterialException(path);

        Material material = Material.matchMaterial(matName.toUpperCase()); // checkea si es valido el material
        if (material == null) throw new InvalidMaterialException(matName, path);

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta!=null){
            // ---------------Name------------------
            String strName = section.getString("Name");
            if (strName != null){
                Component colorName = U.componentColor(strName);
                meta.displayName(colorName);
            }

            // ---------lore (Hay que pasar de List<String> a list component--------------
            List<String> strLore = section.getStringList("Lore");
            if (!strLore.isEmpty()){
                List<Component> compLore = new ArrayList<>();
                for (String line : strLore){
                    Component colorLore = U.componentColor(line);
                    compLore.add(colorLore);
                }
                meta.lore(compLore);
            }

            // ---------------------encantamientos----------------------
            ConfigurationSection enchSection = section.getConfigurationSection("Enchants");
            if (enchSection!=null){
                for (String originalEnchKey : enchSection.getKeys(false)){
                    String enchKey = originalEnchKey.toLowerCase();
                    NamespacedKey key = NamespacedKey.minecraft(enchKey);

                    try{ // no es lo óptimo, pero por si acasoooooo
                        RegistryAccess regAccess = RegistryAccess.registryAccess();
                        Registry<Enchantment> registry = regAccess.getRegistry(RegistryKey.ENCHANTMENT);

                        Enchantment ench = registry.get(key);

                        int lvl = enchSection.getInt(originalEnchKey);
                        if (ench!=null) {
                            meta.addEnchant(ench, lvl, true);
                        }
                    }catch (Exception e) {
                        U.mensajeConsolaNP("&cError con encantamientos en '"+path+"'. &eDetails: "+e);
                        continue;}
                }
            }


            // --------------atributos----------------------
            ConfigurationSection attrSection = section.getConfigurationSection("Attributes");
            if (attrSection!=null){
                Multimap<Attribute, AttributeModifier> allModifiers = HashMultimap.create();
                for (String attrKey : attrSection.getKeys(false)){
                    ConfigurationSection singleAttr = attrSection.getConfigurationSection(attrKey);
                    if (singleAttr != null) {
                        attrKey = attrKey.toLowerCase();
                        try{
                            NamespacedKey key = NamespacedKey.minecraft(attrKey);

                            RegistryAccess regAccess = RegistryAccess.registryAccess();
                            Registry<Attribute> registry = regAccess.getRegistry(RegistryKey.ATTRIBUTE);
                            double value = singleAttr.getDouble("value");
                            String operation = singleAttr.getString("operation");
                            String slot = singleAttr.getString("slot");

                            if (operation==null) continue;
                            operation = operation.toUpperCase();
                            if (slot==null) slot = "MAINHAND";

                            Attribute attr = registry.get(key);Multimap<Attribute, AttributeModifier> atributo;
                            AttributeModifier.Operation modOp = AttributeModifier.Operation.valueOf(operation.toUpperCase());
                            EquipmentSlotGroup modSlot = ItemUtils.getSlotFromString(slot);

                            AttributeModifier modFinal = new AttributeModifier(key, value, modOp, modSlot);

                            allModifiers.put(attr, modFinal);

                        }catch(Exception e){
                            U.mensajeConsolaNP("&cHa habido algún error cargando atributos con el item: "+name+" | key: "+ attrKey);
                            continue;
                        }
                    }
                }
                meta.setAttributeModifiers(allModifiers);
            }
            //------FOODD------------
            ConfigurationSection foodSection = section.getConfigurationSection("Food");
            if (foodSection!=null){
                FoodComponent toSend = item.getItemMeta().getFood();
                try{

                    if (toSend==null){
                        U.mensajeConsolaNP("&cNo se pudo cargar datos de comida en "+name+".");
                    }
                    else {
                        boolean alwaysEat = foodSection.getBoolean("always_eatable");
                        int nutrition = foodSection.getInt("nutrition");
                        double saturationDB = foodSection.getDouble("saturation");
                        float saturation = (float) saturationDB;
                        toSend.setCanAlwaysEat(alwaysEat);
                        toSend.setNutrition(nutrition);
                        toSend.setSaturation(saturation);
                        meta.setFood(toSend);
                    }
                }catch (Exception e){
                    U.mensajeConsolaNP("&cHubo un problema intentando cargar los atributos de comida de: "+name);
                    U.mensajeConsolaNP(e.toString());
                }
            }
            //----UNBREAKABLE-----
            boolean unbreakStatus = section.getBoolean("Unbreakable");
            if (unbreakStatus){
                meta.setUnbreakable(true);
            }
            //-----MAX STACK SIZE--------
            int max_stack = section.getInt("Max_stack_size");
            if(max_stack!=0){
                if(max_stack>99||max_stack<0){
                    U.mensajeConsolaNP("&cHubo un problema intentando cargar Max_stack_size en "+name+". Value interpretado: "+max_stack);
                }else meta.setMaxStackSize(max_stack);
            }

            //---------CONSUMIBLE--------
            ConfigurationSection consumableSection = section.getConfigurationSection("Consumable");
            if (consumableSection!=null){
                consToAdd = ItemUtils.consumableBuilderU(consumableSection, name);
                
                modifyData = true;
            }
            //---------Persistent Data--------
            String stringPData = section.getString("Persistent_data", null);
            if(stringPData!=null){
                String[] arPData = stringPData.split("\\.");
                if(arPData.length!=2){
                    U.mensajeConsolaNP("&cError al intentar cargar Persistent_data en "+name);
                    U.mensajeConsolaNP("&cEl input no es de dos valores | value: "+stringPData+" ("+arPData.length+")");
                }else{
                    NamespacedKey key = new NamespacedKey(plugin, arPData[0]);
                    meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, arPData[1]);
                }
            }
            //---------------
            item.setItemMeta(meta);
            
            if (modifyData) item.setData(DataComponentTypes.CONSUMABLE, consToAdd); // cosito al final para consumible
        }
        return item;
    }
    public static void removeItem(String name){
        String path = "Items."+name;
        FileConfiguration itemcfg = configItem.getConfig();

        if (!itemcfg.contains(path, false)){
            throw new InvalidItemConfigException(path, "Tu path no existe.");
        }

        itemcfg.set(path, null);
        configItem.saveConfig();
    }

    public static Set<String> getItems(){
        FileConfiguration cfg = configItem.getConfig();
        ConfigurationSection section = cfg.getConfigurationSection("Items");

        if (section==null) throw new ItemSectionEmpty("Problema en tu Items.yml, Maybe 'Items:' doesn't exists.");

        return section.getKeys(false);
    }
}
