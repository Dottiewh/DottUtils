package mp.dottiewh.Items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import mp.dottiewh.DottUtils;
import mp.dottiewh.Items.Exceptions.InvalidItemConfigException;
import mp.dottiewh.Items.Exceptions.InvalidMaterialException;
import mp.dottiewh.Items.Exceptions.ItemSectionEmpty;
import mp.dottiewh.Items.Exceptions.MissingMaterialException;
import mp.dottiewh.Utils.ItemUtils;
import mp.dottiewh.Utils.U;
import mp.dottiewh.config.CustomConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Registry;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.registry.RegistryAware;
import org.checkerframework.checker.units.qual.A;
import org.w3c.dom.Attr;

import java.util.*;

public class ItemConfig{
    //private static CustomConfig configMsg;
    private static CustomConfig configItem;
    //private static String prefix;



    public static void itemConfigInit(){
        //configMsg = DottUtils.getRegisteredMsgConfig();
        configItem = DottUtils.getRegisteredItemConfig();
        //prefix = U.getMsgPath("item_prefix");
    }

    public static void saveItem(String name, ItemStack item){ //path something like = Items.ItemName
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

            if (meta.hasFood()){
                ConfigurationSection foodSection = section.createSection("Food");
                FoodComponent food = meta.getFood();
                boolean alwaysEat = food.canAlwaysEat();
                int nutrition = food.getNutrition();
                float saturation = food.getSaturation();


                foodSection.set("Always_Eatable", alwaysEat);
                foodSection.set("Nutrition", nutrition);
                foodSection.set("Saturation", saturation);
            }
        }
        configItem.saveConfig();
    }
    public static ItemStack loadItem(String name) { //path something like = Items.ItemName
        String path = "Items."+name;
        ConfigurationSection section = configItem.getConfig().getConfigurationSection(path);
        if (section==null){
            throw new InvalidItemConfigException(path, "Tu path no existe.");
        }

        // Material
        String matName = section.getString("Material"); //checkea si existe el path
        if (matName == null) throw new MissingMaterialException(path);

        Material material = Material.matchMaterial(matName); // checkea si es valido el material
        if (material == null) throw new InvalidMaterialException(matName, path);

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta!=null){
            // ---------------LORE------------------
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
                for (String enchKey : enchSection.getKeys(false)){
                    NamespacedKey key = NamespacedKey.minecraft(enchKey);

                    try{ // no es lo óptimo, pero por si acasoooooo
                        RegistryAccess regAccess = RegistryAccess.registryAccess();
                        Registry<Enchantment> registry = regAccess.getRegistry(RegistryKey.ENCHANTMENT);

                        Enchantment ench = registry.get(key);

                        int lvl = enchSection.getInt(enchKey);
                        if (ench!=null) {
                            meta.addEnchant(ench, lvl, true);
                        }
                    }catch (Exception e) {
                        U.STmensajeConsolaNP("&cError con encantamientos en '"+path+"'. &eDetails: "+e);
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
                        try{
                            NamespacedKey key = NamespacedKey.minecraft(attrKey);

                            RegistryAccess regAccess = RegistryAccess.registryAccess();
                            Registry<Attribute> registry = regAccess.getRegistry(RegistryKey.ATTRIBUTE);
                            double value = singleAttr.getDouble("value");
                            String operation = singleAttr.getString("operation");
                            String slot = singleAttr.getString("slot");

                            if (operation==null) continue;
                            if (slot==null) slot = "MAINHAND";

                            Attribute attr = registry.get(key);Multimap<Attribute, AttributeModifier> atributo;
                            AttributeModifier.Operation modOp = AttributeModifier.Operation.valueOf(operation.toUpperCase());
                            EquipmentSlotGroup modSlot = ItemUtils.getSlotFromString(slot);

                            AttributeModifier modFinal = new AttributeModifier(key, value, modOp, modSlot);

                            allModifiers.put(attr, modFinal);

                        }catch(Exception e){
                            U.STmensajeConsolaNP("&cHa habido algún error cargando atributos con el item: "+name+" | key: "+ attrKey);
                            continue;
                        }
                    }
                }
                meta.setAttributeModifiers(allModifiers);
            }
            ConfigurationSection foodSection = section.getConfigurationSection("Food");
            if (foodSection!=null){
                FoodComponent toSend = item.getItemMeta().getFood();
                try{

                    if (toSend==null){
                        U.STmensajeConsolaNP("&cNo se pudo cargar datos de comida en "+name+".");
                    }
                    else {
                        boolean alwaysEat = foodSection.getBoolean("Always_Eatable");
                        int nutrition = foodSection.getInt("Nutrition");
                        double saturationDB = foodSection.getDouble("Saturation");
                        float saturation = (float) saturationDB;
                        toSend.setCanAlwaysEat(alwaysEat);
                        toSend.setNutrition(nutrition);
                        toSend.setSaturation(saturation);
                        meta.setFood(toSend);
                    }
                }catch (Exception e){
                    U.STmensajeConsolaNP("&cHubo un problema intentando cargar los atributos de comida de: "+name);
                    U.STmensajeConsolaNP(e.toString());
                }
            }

            item.setItemMeta(meta);
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
