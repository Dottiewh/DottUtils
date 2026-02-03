package mp.dottiewh.items;

import com.destroystokyo.paper.ParticleBuilder;
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
import mp.dottiewh.commands.Commands;
import mp.dottiewh.items.exceptions.*;
import mp.dottiewh.utils.ItemUtils;
import mp.dottiewh.utils.U;
import mp.dottiewh.config.CustomConfig;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.*;
import io.papermc.paper.datacomponent.item.Consumable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.tag.DamageTypeTags;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
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

    public static void saveItem(@NotNull String name, @NotNull ItemStack item, @Nullable String fileName) throws InvalidItemConfigException{
        if(fileName!=null){
            CustomConfig customConfig = new CustomConfig(fileName+".yml", "items", DottUtils.getInstance(), false);
            customConfig.registerConfig();
            if(!customConfig.getFile().exists()) throw new InvalidItemFile("El archivo al que se intenta guardar el item "+name+" no existe! "+fileName);
            saveItem(name, item, customConfig, null);

        }else saveItem(name, item, configItem, null);
    }
    public static void saveItem(@NotNull String name, @NotNull ItemStack item, CustomConfig config, @Nullable String pDataKey) throws InvalidItemConfigException{ //path something like = Items.ItemName
        ConfigurationSection section = config.getConfig().createSection("Items."+name);

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
            //----glider----
            if(meta.isGlider()) section.set("Glider", true);
            //----Enchantment override----
            if(meta.hasEnchantmentGlintOverride()) section.set("Enchantment_glint_override", true);
            //----hide tooltip----
            if(meta.isHideTooltip()) section.set("Hide_tooltip", true);
            //----fire resistant----
            if(meta.hasDamageResistant()){
                Tag<DamageType> damageTag = meta.getDamageResistant();
                if(damageTag!=null){
                    if(damageTag.equals(DamageTypeTags.IS_FIRE)) section.set("Fire_resistant", true);
                }
            }
            //----enchantable----
            if(meta.hasEnchantable())section.set("Enchantable", meta.getEnchantable());
            //----jukebox playable
            if(meta.hasJukeboxPlayable()){
                JukeboxPlayableComponent jukeboxPlayableComponent = meta.getJukeboxPlayable();
                section.set("Jukebox_playable", jukeboxPlayableComponent.getSongKey().toString());
            }
            //----item model----
            if(meta.hasItemModel()){
                NamespacedKey modelString = meta.getItemModel();
                if(modelString!=null) section.set("Model", modelString.toString());
            }
            //----tooltip style-----
            if(meta.hasTooltipStyle()){
                NamespacedKey tooltipStyle = meta.getTooltipStyle();
                if(tooltipStyle!=null) section.set("ToolTip_style", tooltipStyle.toString());
            }
            //----use cooldown----
            if(meta.hasUseCooldown()){
                UseCooldownComponent cooldownComponent = meta.getUseCooldown();
                NamespacedKey cGroup = cooldownComponent.getCooldownGroup();
                ConfigurationSection cooldownSection = section.createSection("UseCooldown");

                if(cGroup!=null)  cooldownSection.set("cooldown_group", cGroup.toString());
                cooldownSection.set("cooldown_seconds", cooldownComponent.getCooldownSeconds());
            }
            //---- TOOL THINGS-----
            if(meta.hasTool()){
                ConfigurationSection toolSection = section.createSection("Tool");
                ToolComponent toolComponent = meta.getTool();

                toolSection.set("damage_per_block", toolComponent.getDamagePerBlock());
                toolSection.set("default_mining_speed", toolComponent.getDefaultMiningSpeed());

                List<ToolComponent.ToolRule> toolRuleList = toolComponent.getRules();
                if(!toolRuleList.isEmpty()){
                    ConfigurationSection rulesSection = toolSection.createSection("rules");
                    int i=1;
                    for(ToolComponent.ToolRule toolRule : toolRuleList){
                        ConfigurationSection ruleSec = rulesSection.createSection("rule_"+i);
                        ruleSec.set("correct_for_drops", toolRule.isCorrectForDrops());
                        ruleSec.set("speed", toolRule.getSpeed());
                        Collection<Material> materialCollection = toolRule.getBlocks();
                        List<String> materialStringList = new LinkedList<>();
                        materialCollection.forEach(material -> materialStringList.add(material.toString()));
                        ruleSec.set("applies", materialStringList);
                        i++;
                    }
                }
            }

            //------MAXSTACKSIZE---------------
            if (meta.hasMaxStackSize()){
                int max = meta.getMaxStackSize();
                section.set("Max_stack_size", max);
            }
            // Persistent data container
            PersistentDataContainer pContainer = meta.getPersistentDataContainer();
            if(pDataKey!=null){
                String output = pContainer.get(new NamespacedKey(plugin, pDataKey), PersistentDataType.STRING);
                if(output!=null){
                    section.set("Persistent_data", pDataKey+"."+output);
                }
            }
            if(!pContainer.isEmpty()){
                ConfigurationSection pDataSection = section.createSection("Persistent_data_container");
                for(NamespacedKey namespacedKey : pContainer.getKeys()){
                    String value = pContainer.get(namespacedKey, PersistentDataType.STRING);
                    if(value==null) continue;
                    pDataSection.set(namespacedKey.toString(), value);
                }
            }
            //---------EQUIPPABLE--------
            if(meta.hasEquippable()){
                ConfigurationSection equippableSection = section.createSection("Equippable");
                EquippableComponent equipComp = meta.getEquippable();

                EquipmentSlot eSlot = equipComp.getSlot();
                equippableSection.set("slot", eSlot.toString());

                Registry<@NotNull Sound> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT);
                equippableSection.set("equip_sound", registry.getKey(equipComp.getEquipSound()).getKey());

                NamespacedKey modelKey = equipComp.getModel();
                if(modelKey!=null) equippableSection.set("asset_id", modelKey.toString());

                if(!equipComp.isDispensable()) equippableSection.set("dispensable", false);
                if(equipComp.isEquipOnInteract()) equippableSection.set("equip_on_interact", true);
                if(!equipComp.isSwappable()) equippableSection.set("swappable", false);
                if(!equipComp.isDamageOnHurt()) equippableSection.set("damage_on_hurt", false);

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
        config.saveConfig();
    }

    @NotNull
    public static ItemStack loadItem(@NotNull String name, @Nullable String fileName){
        if(fileName!=null){
            CustomConfig customConfig = new CustomConfig(fileName+".yml", "items", DottUtils.getInstance(), false);
            customConfig.registerConfig();
            if(!customConfig.getFile().exists()) throw new InvalidItemFile("No se ha podido cargar el item "+name+", ya que no existe el archivo "+fileName);
            return loadItem(name, customConfig);

        }else return loadItem(name, configItem);
    }

    @NotNull
    public static ItemStack loadItem(@NotNull String name, @NotNull CustomConfig iConfig) throws InvalidItemConfigException, MissingMaterialException{ //path something like = Items.ItemName
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
                            Registry<@NotNull Attribute> registry = regAccess.getRegistry(RegistryKey.ATTRIBUTE);
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
            //----glider----
            boolean gliderStatus = section.getBoolean("Glider");
            if(gliderStatus) meta.setGlider(true);
            //----Enchantment override----
            boolean enchantmentStatus = section.getBoolean("Enchantment_glint_override");
            if (enchantmentStatus) meta.setEnchantmentGlintOverride(true);
            //----hide tooltip----
            boolean hideTooltipStatus = section.getBoolean("Hide_tooltip");
            if(hideTooltipStatus) meta.setHideTooltip(true);
            //----Fire resistant----
            boolean fireResistantStatus = section.getBoolean("Fire_resistant");
            if(fireResistantStatus) meta.setDamageResistant(DamageTypeTags.IS_FIRE);
            //-------enchantable-----
            int enchantableOutput = section.getInt("Enchantable", -1);
            if(enchantableOutput>0) meta.setEnchantable(enchantableOutput);
            //------ jukebox playable-----
            String jukeboxOutput = section.getString("Jukebox_playable");
            if(jukeboxOutput!=null){
                String[] jukeboxOutputArray = jukeboxOutput.split(":");

                JukeboxPlayableComponent jukeComponent = meta.getJukeboxPlayable();
                jukeComponent.setSongKey(new NamespacedKey(jukeboxOutputArray[0], jukeboxOutputArray[1]));
                meta.setJukeboxPlayable(jukeComponent);
            }

            //--------equippable----
            ConfigurationSection equipSection = section.getConfigurationSection("Equippable");
            if(equipSection!=null){
                EquippableComponent equipComp = meta.getEquippable();

                String sSlot = equipSection.getString("slot");
                if(sSlot==null) U.mensajeConsola("&cError al intentar equippable slot al cargar "+name+"! saltando propiedades...");
                else{
                    equipComp.setSlot(EquipmentSlot.valueOf(sSlot.toUpperCase()));

                    String sSound = equipSection.getString("equip_sound", "item.armor.equip_generic");
                    Registry<@NotNull Sound> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT);
                    Sound equipSound = registry.get(NamespacedKey.minecraft(sSound.toLowerCase()));
                    equipComp.setEquipSound(equipSound);

                    String sModel = equipSection.getString("asset_id", null);
                    if(sModel!=null){
                        String[] sModelArray = sModel.split(":");
                        NamespacedKey modelKey = new NamespacedKey(sModelArray[0], sModelArray[1]);
                        equipComp.setModel(modelKey);
                    }

                    equipComp.setDispensable(equipSection.getBoolean("dispensable", true));
                    equipComp.setEquipOnInteract(equipSection.getBoolean("equip_on_interact", false));
                    equipComp.setSwappable(equipSection.getBoolean("swappable", true));
                    equipComp.setDamageOnHurt(equipSection.getBoolean("damage_on_hurt", true));
                    meta.setEquippable(equipComp);
                }
            }
            //----- model data------
            String modelString = section.getString("Model");
            if(modelString!=null){
                String[] modelArray = modelString.split(":");
                meta.setItemModel(new NamespacedKey(modelArray[0], modelArray[1]));
            }
            //---- tooltip style-----
            String tooltipStyle = section.getString("ToolTip_style");
            if(tooltipStyle!=null){
                String[] tooltipStyleArray = tooltipStyle.split(":");
                meta.setTooltipStyle(new NamespacedKey(tooltipStyleArray[0], tooltipStyleArray[1]));
            }
            //---- use cooldown----
            ConfigurationSection cooldownSection = section.getConfigurationSection("UseCooldown");
            if(cooldownSection!=null){
                String cGroup = cooldownSection.getString("cooldown_group");
                float cSeconds = (float) cooldownSection.getDouble("cooldown_seconds", -1);
                UseCooldownComponent cooldownComponent = meta.getUseCooldown();

                if(cGroup!=null){

                    String[] cGroupArray = cGroup.split(":");
                    cooldownComponent.setCooldownGroup(new NamespacedKey(cGroupArray[0], cGroupArray[1]));
                }
                if(cSeconds>=0) cooldownComponent.setCooldownSeconds(cSeconds);
                meta.setUseCooldown(cooldownComponent);
            }
            //---- Tool data----
            ConfigurationSection toolSection = section.getConfigurationSection("Tool");
            if(toolSection!=null){
                ToolComponent toolComponent = meta.getTool();
                toolComponent.setDamagePerBlock(toolSection.getInt("damage_per_block", 1));
                float defaultSpeed = (float) toolSection.getDouble("default_mining_speed", -1);
                if(defaultSpeed>=0) toolComponent.setDefaultMiningSpeed(defaultSpeed);

                ConfigurationSection rulesSection = toolSection.getConfigurationSection("rules");
                if(rulesSection!=null){
                    Set<String> ruleKeysList = rulesSection.getKeys(false);
                    for(String ruleKey : ruleKeysList){
                        ConfigurationSection ruleSection = rulesSection.getConfigurationSection(ruleKey);
                        if(ruleSection==null) continue;

                        boolean correct_drops = ruleSection.getBoolean("correct_for_drops");
                        float speed = (float) ruleSection.getDouble("speed");
                        List<String> materialStringList = ruleSection.getStringList("applies");
                        List<Material> materialList = new LinkedList<>();
                        materialStringList.forEach(blockString-> materialList.add(Material.valueOf(blockString.toUpperCase())));

                        toolComponent.addRule(materialList, speed, correct_drops);
                        U.mensajeDebugConsole(correct_drops+" | "+speed+" | "+materialStringList);
                    }
                }else U.mensajeConsola("&cNo se ha podido cargar las reglas sobre Tool al cargar "+name);
                meta.setTool(toolComponent);
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
            ConfigurationSection sectionPData = section.getConfigurationSection("Persistent_data_container");
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
            if(sectionPData!=null){
                for(String spacedKey : sectionPData.getKeys(false)) {
                    String value = sectionPData.getString(spacedKey);
                    if(value!=null){
                        String[] arrayKey = spacedKey.split(":", 2);
                        NamespacedKey namespacedKey = new NamespacedKey(arrayKey[0], arrayKey[1]);
                        meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
                    }
                    else U.mensajeConsola("&cNo se ha podido cargar "+spacedKey+"! en "+name);
                }
            }

            //==================CUSTOMDATA================
            ConfigurationSection customDataSection = section.getConfigurationSection("CustomData");
            if(customDataSection!=null){
                ConfigurationSection onAttack = customDataSection.getConfigurationSection("onAttack");
                ConfigurationSection onKill = customDataSection.getConfigurationSection("onKill");
                ConfigurationSection onShot = customDataSection.getConfigurationSection("onShot");
                ConfigurationSection onFish = customDataSection.getConfigurationSection("onFish");

                U.mensajeDebugConsole("customDataSection");
                //ON ATTACK
                if(onAttack!=null){
                    U.mensajeDebugConsole("onAttack");
                    String effect = onAttack.getString("effectapply", "");
                    String particle = onAttack.getString("particles", "");
                    ItemUtils.addPersistentDataString(meta, "onAttack_effect", effect);
                    ItemUtils.addPersistentDataString(meta, "onAttack_particle", particle);
                    //U.mensajeDebugConsole(effect+ " | "+particle);
                }
                if(onKill!=null){
                    U.mensajeDebugConsole("onKill");
                    String particle = onKill.getString("particles", "");
                    ItemUtils.addPersistentDataString(meta, "onKill_particle", particle);
                }
                if(onShot!=null){
                    U.mensajeDebugConsole("onShot");
                    String particle = onShot.getString("particles", "");
                    ItemUtils.addPersistentDataString(meta, "onShot_particle", particle);
                }
                if(onFish!=null){
                    U.mensajeDebugConsole("onFish");
                    String particle = onFish.getString("particles", "");
                    ItemUtils.addPersistentDataString(meta, "onFish_particle", particle);
                }
            }

            //---------------
            item.setItemMeta(meta);
            
            if (modifyData) item.setData(DataComponentTypes.CONSUMABLE, consToAdd); // cosito al final para consumible
        }
        U.mensajeDebugConsole("load item");
        return item;
    }
    @Nullable
    public static ParticleBuilder loadParticleData(ItemMeta meta, String key, @Nullable Location trailLocation){
        String output = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, key+"_particle"), PersistentDataType.STRING);
        if(output==null){
            U.mensajeDebugConsole("output null "+key+"_particle");
            return null;
        }
        if(output.isEmpty()) return null;

        String[] outputArray = output.split(";");
        List<String> updatedOutput = new ArrayList<>(Arrays.stream(outputArray).toList());
        String sID = updatedOutput.removeFirst();
        Particle particle = Particle.valueOf(sID.toUpperCase());

        boolean hasCustomProperties = outputArray.length>7;
        ParticleBuilder builder = new ParticleBuilder(particle);

        Color color=null;
        if(hasCustomProperties){
            int red = Integer.parseInt(updatedOutput.removeFirst());
            int green = Integer.parseInt(updatedOutput.removeFirst());
            int blue = Integer.parseInt(updatedOutput.removeFirst());
            color= Color.fromRGB(red,green,blue);
        }

        double offsetX = Double.parseDouble(updatedOutput.removeFirst());
        double offsetY = Double.parseDouble(updatedOutput.removeFirst());
        double offsetZ = Double.parseDouble(updatedOutput.removeFirst());
        builder.offset(offsetX, offsetY, offsetZ);

        if(hasCustomProperties) {
            switch (particle){
                case DUST -> {
                    float size = Float.parseFloat(updatedOutput.removeFirst());
                    Particle.DustOptions dustOptions = new Particle.DustOptions(color, size);
                    builder.data(dustOptions);
                }
                case TRAIL -> {
                    if(trailLocation==null){
                        U.mensajeConsola("&cSe ha intentado resolver una particula trail, pero no es apta en este caso.");
                        return null;
                    }
                    int durationTicks = Integer.parseInt(updatedOutput.removeFirst());
                    Particle.Trail trailOptions = new Particle.Trail(trailLocation, color, durationTicks);
                    builder.data(trailOptions);
                }
                default -> updatedOutput.removeFirst();
            }
        }else{
            double speed = Double.parseDouble(updatedOutput.removeFirst());
            builder.extra(speed);
        }

        int count = Integer.parseInt(updatedOutput.removeFirst());
        builder.count(count);

        boolean force = false;
        if(!updatedOutput.isEmpty()) force = Boolean.parseBoolean(updatedOutput.removeFirst());
        builder.force(force);

        //U.mensajeDebugConsole(builder.toString() + " | "+force);
        return builder;
    }
    @Nullable
    public static PotionEffect loadPotionEffect(ItemMeta meta, String key){
        String dataString = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, key+"_effect"), PersistentDataType.STRING);
        if (dataString==null) return null;
        if(dataString.isEmpty()) return null;
        String[] outputArray = dataString.split(";");
        boolean simple = outputArray.length<=3;
        List<String> outputList = new ArrayList<>(Arrays.asList(outputArray));

        String effectTypeString = outputList.removeFirst().toLowerCase();
        Registry<@NotNull PotionEffectType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT);
        PotionEffectType effectType = registry.get(NamespacedKey.minecraft(effectTypeString));
        if(effectType==null){
            U.mensajeConsola("&cNo se ha podido resolver el effectType: "+effectTypeString);
            return null;
        }


        int duration = Integer.parseInt(outputList.removeFirst());
        int amplifier = Integer.parseInt(outputList.removeFirst());
        if(simple){
            return new PotionEffect(effectType, duration, amplifier);
        }else{
            boolean ambient = Boolean.parseBoolean(outputList.removeFirst());
            boolean particles = Boolean.parseBoolean(outputList.removeFirst());
            boolean icon = Boolean.parseBoolean(outputList.removeFirst());
            return new PotionEffect(effectType, duration, amplifier, ambient, particles, icon);
        }
    }

    //
    public static boolean existsItem(String name){
        String path = "Items."+name;
        FileConfiguration itemcfg = configItem.getConfig();
        return itemcfg.contains(path, false);
    }
    public static void removeItem(String name, @Nullable String fileName){
        String path = "Items."+name;
        FileConfiguration itemcfg =null;
        CustomConfig tempConfig = configItem;

        if(fileName!=null){
            tempConfig = new CustomConfig(fileName+".yml", "items", DottUtils.getInstance(), false);
            tempConfig.registerConfig();
            if(!tempConfig.getFile().exists()) throw new InvalidItemFile("El file "+fileName+" no existe al intentar borrar "+name+"!");
            itemcfg=tempConfig.getConfig();

        }else itemcfg = tempConfig.getConfig();

        if (!itemcfg.contains(path, false)){
            throw new InvalidItemConfigException(path, "Tu path no existe.");
        }

        itemcfg.set(path, null);
        tempConfig.saveConfig();
        Commands.reloadBrigadierItems();
    }

    @NotNull
    public static Set<String> getItems(){
        FileConfiguration cfg = configItem.getConfig();
        ConfigurationSection section = cfg.getConfigurationSection("Items");

        if (section==null) throw new ItemSectionEmpty("Problema en tu Items.yml, Maybe 'Items:' doesn't exists.");
        Set<String> itemList = new HashSet<>(section.getKeys(false));

        File itemFolder = new File(plugin.getDataFolder(), File.separator+"items");
        if(itemFolder.exists()){
            DottUtils instance = DottUtils.getInstance();
            String[] fileNames = itemFolder.list();
            if(fileNames!=null){
                for(String fName : fileNames) {
                    CustomConfig tempConfig = new CustomConfig(fName, "items", instance, false);
                    tempConfig.registerConfig();
                    if (!tempConfig.getFile().exists()) continue;
                    ConfigurationSection tempSection = tempConfig.getConfig().getConfigurationSection("Items");
                    if (tempSection == null) continue;

                    Set<String> keySet = tempSection.getKeys(false);
                    if(fName.endsWith(".yml")) fName=fName.replace(".yml", "");
                    String finalFName = fName;
                    keySet.forEach(s->itemList.add(finalFName +"."+s));
                }
            }
        }

        return itemList;
    }
    @Nullable
    public static ItemStack getInternalItem(@NotNull String itemName){
        ItemStack toDeliver;
        try{
            toDeliver = loadItem(itemName, DottUtils.ymlInternalItems);
        } catch (InvalidItemConfigException e) {
            toDeliver = null;
            U.mensajeDebugConsole("&cA ocurrido una excepción al buscar "+itemName+"! | &4"+e);
        }
        return toDeliver;
    }
    @NotNull
    public static ItemStack getInternalItem(@NotNull String itemName, @NotNull ItemStack def){
        ItemStack item = getInternalItem(itemName);
        if(item==null) {
            saveItem(itemName, def, DottUtils.ymlInternalItems, null);
            U.mensajeConsolaNP("&eNo existía el item interno &4"+itemName+" &eregenerando automaticamente...");
            return def;
        }
        return item;
    }
    @NotNull
    public static ItemStack getInternalItem(@NotNull String itemName, @NotNull Material m, @Nullable String display, @Nullable List<Component> lore){
        ItemStack success = getInternalItem(itemName);
        if(success!=null) return success;

        ItemStack itemDefault = new ItemStack(m);
        ItemMeta meta = itemDefault.getItemMeta();
        if(display!=null){
            Component dName = U.componentColor(display);
            meta.displayName(dName);
        }
        if(lore!=null){
            meta.lore(lore);
        }
        itemDefault.setItemMeta(meta);
        saveItem(itemName, itemDefault, DottUtils.ymlInternalItems, null);
        U.mensajeConsolaNP("&eNo existía el item interno &4"+itemName+" &eregenerando automaticamente...");
        return itemDefault;
    }
    @NotNull
    public static ItemStack getInternalItem(@NotNull String itemName, @NotNull Material m, @Nullable String display, @Nullable List<Component> lore, @NotNull String pDataKey, @NotNull String pDataValue){
        ItemStack success = getInternalItem(itemName);
        if(success!=null) return success;

        ItemStack itemDefault = new ItemStack(m);
        ItemMeta meta = itemDefault.getItemMeta();
        if(display!=null){
            Component dName = U.componentColor(display);
            meta.displayName(dName);
        }
        if(lore!=null){
            meta.lore(lore);
        }
        PersistentDataContainer pData = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, pDataKey);
        pData.set(key, PersistentDataType.STRING, pDataValue);

        itemDefault.setItemMeta(meta);
        saveItem(itemName, itemDefault, DottUtils.ymlInternalItems, pDataKey);
        U.mensajeConsolaNP("&eNo existía el item interno &4"+itemName+" &eregenerando automaticamente...");
        return itemDefault;
    }
}
