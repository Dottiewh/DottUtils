package mp.dottiewh.features.crafts;

import mp.dottiewh.DottUtils;
import mp.dottiewh.features.items.Exceptions.InvalidItemConfigException;
import mp.dottiewh.features.items.ItemConfig;
import mp.dottiewh.utils.U;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

public class Craft{
    private static Plugin plugin = DottUtils.getPlugin();
    private static Server server = Bukkit.getServer();
    private static FileConfiguration fileC;
    private static ConfigurationSection fSection;

    public static void init(){
        fileC = DottUtils.ymlCrafts.getConfig();
        fSection = fileC.getConfigurationSection("Crafts");
        assert fSection!=null;

        for(String keys : fSection.getKeys(false)){
            load(keys);
        }
        String displayKeys = String.join("&f, &9", fSection.getKeys(false));
        U.mensajeConsola("&eIntentando cargas las recetas: &9"+displayKeys);
    }

    public static boolean load(String name){
        boolean toGive=true;

        NamespacedKey key = new NamespacedKey(plugin, name);
        if(server.getRecipe(key)!=null){
            toGive=false;
            server.removeRecipe(key);
        }

        mainProcessOfLoad(name);
        return toGive;
    }
    private static void mainProcessOfLoad(String name){
        ConfigurationSection section = fileC.getConfigurationSection("Crafts."+name);
        if (section==null) return; //podemos meter una excepción aquí

        ItemStack[] slots = new ItemStack[9];
        // recorrido de slots
        for(int i=0; i<slots.length; i++){
            String rawGot = section.getString("Slot"+(i+1));
            if(rawGot==null){
                slots[i] = null;
                continue;
            }
            String[] gotArray = rawGot.split(";", 2);
            String got = gotArray[0];

            ItemStack item;
            try{
                item = ItemConfig.loadItem(got);
                slots[i] = item;
            }catch (InvalidItemConfigException e) {
                Material material = Material.getMaterial(got.toUpperCase());
                if (material == null) {
                    slots[i] = null;
                    continue;
                }
                item = new ItemStack(material, 1); // CAMBIAR AMOUNT!
                slots[i] = item;
            }
            if (gotArray.length == 2 && slots[i] != null) {
                slots[i].setAmount(Integer.parseInt(gotArray[1]));
                U.mensajeConsolaNP(gotArray[1]);
            }
            U.mensajeConsolaNP(rawGot);
        }
        //
        String toGiveS = section.getString("To_Give");
        if (toGiveS==null) return; // Excepción por aquí
        ItemStack toGive;
        try{
            toGive=ItemConfig.loadItem(toGiveS);
        }catch (InvalidItemConfigException e){
            Material mat = Material.getMaterial(toGiveS.toUpperCase());
            if(mat==null) return; //excepción
            toGive= new ItemStack(mat, 1);
        }
        toGive.setAmount(section.getInt("Amount_To_Give", 1));

        NamespacedKey key = new NamespacedKey(plugin, name);
        ShapedRecipe recipe =new ShapedRecipe(key, toGive);

        String[] shape = new String[] {"123", "456", "789"};

        for (int i = 0; i < slots.length; i++) {
            ItemStack item = slots[i];
            int row = i / 3;
            int col = i % 3;
            char c = (char) ('0' + (i + 1));

            if (item == null || item.getType() == Material.AIR) {
                char[] rowChars = shape[row].toCharArray();
                rowChars[col] = ' ';
                shape[row] = new String(rowChars);
            }
        }
        recipe.shape(shape[0], shape[1], shape[2]);

        for (int i = 0; i < slots.length; i++) {
            ItemStack item = slots[i];
            int row = i / 3;
            int col = i % 3;
            char c = (char) ('0' + (i + 1));

            if (shape[row].charAt(col) == ' ') continue;

            if (item.getAmount() <= 0||item.getAmount()>99) {
                item = item.clone();
                item.setAmount(1);
            }
            recipe.setIngredient(c, new RecipeChoice.ExactChoice(item));
        }



        Bukkit.getServer().addRecipe(recipe);
    }
}
