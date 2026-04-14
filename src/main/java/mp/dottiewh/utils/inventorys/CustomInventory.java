package mp.dottiewh.utils.inventorys;

import mp.dottiewh.utils.U;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Consumer;

public class CustomInventory implements InventoryHolder{

    protected String prefix;
    protected String title;
    protected String prefixAndTitle;
    protected Component prefixAndTitleComponent;
    protected int size;
    protected boolean cancelByDefault = true;

    protected final HashMap<Integer, Consumer<InventoryClickEvent>> clickEventsMap = new HashMap<>();
    protected final HashMap<Integer, ItemStack> itemMap = new HashMap<>();

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
    public Inventory createInventory(){
        Inventory inventory = Bukkit.createInventory(this, size, prefixAndTitleComponent);
        setUp(inventory);
        return inventory;
    }

    private void setUp(Inventory inventory){
        for(int i=0;i<inventory.getSize();i++){
            ItemStack item = itemMap.get(i);
            if(item==null) continue;
            inventory.setItem(i, item);
        }
    }
    public void setItem(int itemSlot, ItemStack item){
        if(item.hasItemMeta()){
            ItemMeta meta = item.getItemMeta();
            NamespacedKey key = new NamespacedKey("dottmenusfront", "slot");
            meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, itemSlot);

            item.setItemMeta(meta);
        }
        itemMap.put(itemSlot, item);
    }
    public void setItemTask(int itemSlot, Consumer<InventoryClickEvent> consumer) {
        clickEventsMap.put(itemSlot, consumer);
    }
    public void doTaskFromItem(ItemStack item, InventoryClickEvent e){
        if(!item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer persistentDataContainer = meta.getPersistentDataContainer();
        Integer slot = persistentDataContainer.get(new NamespacedKey("dottmenusfront", "slot"), PersistentDataType.INTEGER);
        if(slot==null) return;

        Consumer<InventoryClickEvent> consumer = clickEventsMap.get(slot);
        if(consumer==null) return;
        consumer.accept(e);
    }

    public void createInventoryAndOpen(Player player){
        player.openInventory(createInventory());
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
    public boolean isCancelByDefault() {
        return cancelByDefault;
    }
    public void setCancelByDefault(boolean b) {
        this.cancelByDefault = b;
    }


    //--
    @Override
    @ApiStatus.Internal
    public @NotNull Inventory getInventory(){
        return createInventory();
    }

    public static void onItemClick(InventoryClickEvent e){
        if(!((e.getWhoClicked()) instanceof Player player)) return;
        if(e.getClickedInventory()==null) return;
        if(!(e.getClickedInventory().getHolder() instanceof CustomInventory customInventory)) return;

        if(customInventory.isCancelByDefault()){
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
        }

        ItemStack item = e.getCurrentItem();
        if(item!=null){
            customInventory.doTaskFromItem(item, e);
        }
    }
}
