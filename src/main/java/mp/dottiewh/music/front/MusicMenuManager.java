package mp.dottiewh.music.front;

import mp.dottiewh.DottUtils;
import mp.dottiewh.config.CustomConfig;
import mp.dottiewh.items.ItemConfig;
import mp.dottiewh.music.MusicConfig;
import mp.dottiewh.music.MusicMainCommand;
import mp.dottiewh.utils.U;
import mp.dottiewh.utils.inventorys.CustomInventory;
import mp.dottiewh.utils.inventorys.CustomMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MusicMenuManager extends MusicMainCommand {
    private CustomMenu customMenu = null;
    private static final CustomConfig ymlItem = DottUtils.ymlInternalItems;

    public MusicMenuManager() {
        this.customMenu = new CustomMenu(getMainInventory());
    }

    //
    private static Component getComponentTitle(String t){
        return U.componentColor(musicPrefix.replace(" ", "")+" "+t);
    }
    //
    private CustomInventory getMainInventory(Player player){
        return new CustomInventory(getComponentTitle("&f&lMenú"), 9) {
            @Override
            public void setUp(Player player, Inventory inv) {
                ItemStack saves = ItemConfig.loadItem("music_menu_saves", ymlItem);
                int savesAmount = MusicConfig.getMusicList().size();
                if(savesAmount>99) savesAmount=99;
                saves.setAmount(savesAmount);

                ItemStack options = ItemConfig.loadItem("music_menu_options", ymlItem);

                inv.setItem(1, saves);
                inv.setItem(7, options);

            }

            @Override
            public void handleClick(Player player, int slot, ItemStack item) {

            }
        };
    }
}
