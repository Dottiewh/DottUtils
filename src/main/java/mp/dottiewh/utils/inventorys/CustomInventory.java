package mp.dottiewh.utils.inventorys;

import mp.dottiewh.utils.U;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class CustomInventory {

    protected String prefix;
    protected String title;
    protected String prefixAndTitle;
    protected Component prefixAndTitleComponent;
    protected int size;

    public CustomInventory(Component prefixAndTitle, int size){
        this.size=size;
        this.prefixAndTitleComponent=prefixAndTitle;
    }
    public CustomInventory(String prefix, String title, int size) {
        this.prefix = prefix;
        this.title = title;
        this.size = size;
        //
        this.prefixAndTitle=prefix+title;
        this.prefixAndTitleComponent=U.componentColor(prefixAndTitle);
    }
    //
    public Inventory createInventory(Player player){
        Inventory inventory = Bukkit.createInventory(player, size, prefixAndTitleComponent);
        setUp(player, inventory);
        return inventory;
    }

    public abstract void setUp(Player player, Inventory inventory);
    public abstract void handleClick(Player player, int slot, ItemStack item);

    public void open(Player player){
        player.openInventory(createInventory(player));
    }
    //

    public String getPrefix() {
        return prefix;
    }
    public String getTitle() {
        return title;
    }
    public String getPrefixAndTitle() {
        return prefixAndTitle;
    }
    public int getSize() {
        return size;
    }
}
