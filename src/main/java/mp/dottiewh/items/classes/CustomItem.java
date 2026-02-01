package mp.dottiewh.items.classes;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomItem {
    private final ItemStack itemStack;
    private ItemMeta meta;
    private boolean hasCustomProperties;

    private boolean hasOnAttackProperties;



    public CustomItem(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.meta=itemStack.getItemMeta();


    }


    public ItemStack getItemStack() {
        return itemStack;
    }
    public ItemMeta getMeta() {
        return meta;
    }
    public void setMeta(ItemMeta meta){
        this.itemStack.setItemMeta(meta);
        this.meta=meta;
    }
}
