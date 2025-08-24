package mp.dottiewh.Items;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import mp.dottiewh.DottUtils;
import mp.dottiewh.Items.Exceptions.InvalidItemConfigException;
import mp.dottiewh.Items.Exceptions.InvalidMaterialException;
import mp.dottiewh.Items.Exceptions.ItemSectionEmpty;
import mp.dottiewh.Items.Exceptions.MissingMaterialException;
import mp.dottiewh.Utils.U;
import mp.dottiewh.config.CustomConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

            //name
            if (meta.hasDisplayName()) section.set("Name", U.componentToStringMsg(meta.displayName()));
            //lore
            if (meta.hasLore()){
                List<Component> loreComp = meta.lore();
                if (loreComp!= null){
                    List<String> lorePlain = loreComp.stream().map(U::componentToStringMsg).toList(); //cast de comp a string
                    section.set("Lore", lorePlain);
                }
            }
            //Enchants
            if (meta.hasEnchants()){
                ConfigurationSection enchSection = section.createSection("Enchants");
                //guardar cada encantamiento
                meta.getEnchants().forEach((ench, lvl)->
                    enchSection.set(ench.getKey().getKey(), lvl));
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
            // nombre
            String strName = section.getString("Name");
            if (strName != null){
                Component colorName = U.componentColor(strName);
                meta.displayName(colorName);
            }

            // lore (Hay que pasar de List<String> a list component
            List<String> strLore = section.getStringList("Lore");
            if (!strLore.isEmpty()){
                List<Component> compLore = new ArrayList<>();
                for (String line : strLore){
                    Component colorLore = U.componentColor(line);
                    compLore.add(colorLore);
                }
                meta.lore(compLore);
            }

            // encantamientos
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
