package mp.dottiewh.api.directors;

import mp.dottiewh.config.CustomConfig;
import mp.dottiewh.items.ItemConfig;
import mp.dottiewh.items.exceptions.InvalidItemConfigException;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class DuItemsDirector {
    public DuItemsDirector(){}


    public void saveItem(@NotNull String name, @NotNull ItemStack item, @Nullable String fileName) throws InvalidItemConfigException {
        ItemConfig.saveItem(name, item, fileName);
    }
    public void saveItem(@NotNull String name, @NotNull ItemStack item, CustomConfig customConfig) throws InvalidItemConfigException {
        ItemConfig.saveItem(name, item, customConfig, null);
    }
    @NotNull
    public ItemStack getItem(@NotNull String name, @Nullable String fileName) throws InvalidItemConfigException{
        return ItemConfig.loadItem(name, fileName);
    }
    @NotNull
    public ItemStack loadItem(@NotNull String name, @NotNull CustomConfig customConfig) throws InvalidItemConfigException { //path something like = Items.ItemName
        return ItemConfig.loadItem(name, customConfig);
    }

    public boolean existsItem(String id){
        return ItemConfig.existsItem(id);
    }
    public void removeItem(String name, @Nullable String fileName) throws InvalidItemConfigException{
        ItemConfig.removeItem(name, fileName);
    }
    public void removeItem(String name, CustomConfig customConfig) throws InvalidItemConfigException {
        ItemConfig.removeItem(name, customConfig);
    }

    @NotNull
    public Set<String> getItemsFromMainPlugin(){
        return ItemConfig.getItems();
    }
    @Nullable
    public ItemStack getInternalItem(@NotNull String itemName){
        return ItemConfig.getInternalItem(itemName);
    }
    @NotNull
    public static ItemStack getInternalItem(@NotNull String itemName, @NotNull ItemStack def) {
        return ItemConfig.getInternalItem(itemName, def);
    }
}
