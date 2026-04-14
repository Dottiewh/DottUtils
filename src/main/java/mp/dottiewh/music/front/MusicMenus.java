package mp.dottiewh.music.front;

import mp.dottiewh.DottUtils;
import mp.dottiewh.config.CustomConfig;
import mp.dottiewh.items.ItemConfig;
import mp.dottiewh.music.MusicConfig;
import mp.dottiewh.music.MusicMainCommand;
import mp.dottiewh.music.classes.LegacyMusic;
import mp.dottiewh.utils.U;
import mp.dottiewh.utils.inventorys.CustomInventory;
import mp.dottiewh.utils.inventorys.CustomMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MusicMenus extends MusicMainCommand{
    private static final CustomConfig ymlItem = DottUtils.ymlInternalItems;

    public static CustomMenu getMainMenu(){
        CustomMenu menu = new CustomMenu(getMainInventory());

        menu.addChild("saves", getSavesInventory(1));
        menu.addChild("settings", getSettingsInventory());

        return menu;
    }


    //--Inventorys--
    public static CustomInventory getMainInventory(){
        CustomInventory inv = getDefaultCInventory("&f&lMenú", 9);
        ItemStack saves = ItemConfig.loadItem("music_menu_saves", ymlItem);
        int savesAmount = MusicConfig.getMusicList().size();
        if(savesAmount>99) savesAmount=99;
        saves.setAmount(savesAmount);

        ItemStack options = ItemConfig.loadItem("music_menu_options", ymlItem);

        inv.setItem(1, saves);
        inv.setItemTask(1, e->{
            getSavesInventory(1).createInventoryAndOpen((Player) e.getWhoClicked());
        });
        inv.setItem(7, options);
        inv.setItemTask(7, e->{
            getSettingsInventory().createInventoryAndOpen((Player) e.getWhoClicked());
        });
        return inv;
    }
    public static CustomInventory getSavesInventory(int page){
        int maxSize = page*27;
        boolean nextPage = maxSize<MusicConfig.getMusicList().size();

        CustomInventory inv = getDefaultCInventory("&8Musicas registradas &d"+page, 36);

        //ITEMS REGISTRAR
        List<Component> loreList = new ArrayList<>();

        loreList.add(U.componentColor("&8--------------"));
        loreList.add(U.componentColor("&7Click Derecho &8- &fReproducir CON loop"));
        loreList.add(U.componentColor("&7Click Izquierdo &8- &fReproducir SIN loop"));
        loreList.add(U.componentColor("&7Shift + Click Derecho &8- &fReproducir a todos CON loop"));
        loreList.add(U.componentColor("&7Shift + Click Izquierdo &8- &fReproducir a todos SIN loop"));

        MusicFront.addMusicSaves(inv, page, loreList, (e, s)->{
            MusicFront.switchPlayType((Player) e.getWhoClicked(), s, e.getClick());
        });

        for(int i=27;i<36;i++){
            inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        if(page>1){ // get back page
            ItemStack head = ItemConfig.loadItem("music_saves_previouspage", ymlItem);
            SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowLeft"));

            head.setItemMeta(skullMeta);

            inv.setItem(30, head);
            inv.setItemTask(30, e->{
                getSavesInventory(page-1).createInventoryAndOpen((Player) e.getWhoClicked());
            });
        }
        if(nextPage){
            ItemStack head = ItemConfig.loadItem("music_saves_nextpage", ymlItem);
            SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowRight"));

            head.setItemMeta(skullMeta);

            inv.setItem(32, head);
            inv.setItemTask(32, e->{
                getSavesInventory(page+1).createInventoryAndOpen((Player) e.getWhoClicked());
            });
        }
        addBackToMenuArrow(inv, 27);

        ItemStack stopMusic = ItemConfig.loadItem("music_saves_stopmusic", ymlItem);
        inv.setItem(35, stopMusic);
        inv.setItemTask(35, e->{
            MusicFront.switchStopMusic((Player) e.getWhoClicked(), e.getClick());
        });

        return inv;
    }
    private static CustomInventory getSettingsInventory(){
        CustomInventory inv = getDefaultCInventory("&6Reproductor General", 9);

        addBackToMenuArrow(inv, 0);

        ItemStack volItem = ItemConfig.loadItem("music_options_volume", DottUtils.ymlInternalItems);
        ItemMeta volMeta = volItem.getItemMeta();
        List<Component> volLore = volMeta.lore();
        List<Component> overRideVolLore;
        if(volLore==null){
            overRideVolLore=new ArrayList<>();
        }else{
            overRideVolLore=new ArrayList<>(volLore);
        }
        overRideVolLore.add(U.componentColor("&eVol: &f"+U.truncar(MusicConfig.getGlobalVolume(), 2)));
        volMeta.lore(overRideVolLore);
        volItem.setItemMeta(volMeta);

        ItemStack reloadItem = ItemConfig.getInternalItem("music_options_reload", Material.WRITABLE_BOOK,
                "&e&lRecargar la config", null, "musicFrontInternal", "options_reload");

        inv.setItem(3, volItem);
        inv.setItemTask(3, e->{
            MusicFront.switchVolumeMusicOption(e.getClick());
            getSettingsInventory().createInventoryAndOpen((Player) e.getWhoClicked());
        });
        inv.setItem(7, reloadItem);
        inv.setItemTask(7, e->{
            U.targetCommand((Player) e.getWhoClicked(), "du reload");
        });

        return inv;
    }
    //---utils---
    private static CustomInventory addBackToMenuArrow(CustomInventory inv, int slot) {
        ItemStack item = getBackArrow();
        inv.setItem(slot, item);
        inv.setItemTask(slot, e->getMainInventory().createInventoryAndOpen((Player) e.getWhoClicked()));

        return inv;
    }
    private static ItemStack getBackArrow(){
        ItemStack head = ItemConfig.loadItem("music_gotomenu", ymlItem);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowLeft"));

        head.setItemMeta(skullMeta);
        return head;
    }
    private static CustomInventory getDefaultCInventory(String t, int size){
        Component title = U.componentColor(MusicMainCommand.musicPrefix.replace(" ", "")+" "+t);
        return new CustomInventory(title, size);
    }


}
