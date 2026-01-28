package mp.dottiewh.music;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.DottUtils;
import mp.dottiewh.commands.Commands;
import mp.dottiewh.config.CustomConfig;
import mp.dottiewh.items.ItemConfig;
import mp.dottiewh.utils.U;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MusicFront extends MusicMainCommand{
    Player player;
    private static final CustomConfig ymlItem = DottUtils.ymlInternalItems;

    static void buildFront(CommandContext<CommandSourceStack> ctx){
        new MusicFront(ctx);
    }
    private MusicFront(CommandContext<CommandSourceStack> ctx){
        super();
        this.sender=ctx.getSource().getSender();
        if(!(sender instanceof Player p)){
            senderMessageMPr("&cEste comando solo lo puede usar un jugador.");
            return;
        }
        this.player=p;

        run();
    }

    @Override
    protected void run(){
        openMenu(player);
    }

    //
    private static void openMenu(Player player){
        Inventory inv = createInventory(player, "&f&lMenú", 9);
        ItemStack saves = ItemConfig.loadItem("music_menu_saves", ymlItem);
        int savesAmount = MusicConfig.getMusicList().size();
        if(savesAmount>99) savesAmount=99;
        saves.setAmount(savesAmount);

        ItemStack options = ItemConfig.loadItem("music_menu_options", ymlItem);

        inv.setItem(1, saves);
        inv.setItem(7, options);

        player.openInventory(inv);
    }
    private static Inventory loadSavesInventory(Player p, int page){
        int maxSize = page*27;
        List<String> musicArray = MusicConfig.getMusicList();
        boolean nextPage = maxSize<musicArray.size();

        Inventory inv = createInventory(p, "&8Musicas registradas &d"+page, 36);

        //ITEMS REGISTRAR
        int slot=0;
        List<Component> loreList = new ArrayList<>();

        loreList.add(U.componentColor("")); // id 0 o first
        loreList.add(U.componentColor("&8--------------"));
        loreList.add(U.componentColor("&7Click Derecho &8- &fReproducir CON loop"));
        loreList.add(U.componentColor("&7Click Izquierdo &8- &fReproducir SIN loop"));
        loreList.add(U.componentColor("&7Shift + Click Derecho &8- &fReproducir a todos CON loop"));
        loreList.add(U.componentColor("&7Shift + Click Izquierdo &8- &fReproducir a todos SIN loop"));
        for(int i=(page-1)*27;(i<maxSize)&&(i<musicArray.size());i++){
            String songName = musicArray.get(i);
            int tickDuration = MusicConfig.getTicksDuration(songName);
            loreList.set(0, U.componentColor("&6Duración: &e"+tickDuration+" &7("+(tickDuration/20)+"s)"));

            ItemStack item = new ItemStack(MusicConfig.getDisplayMaterial(songName));
            ItemMeta meta = item.getItemMeta();

            meta.displayName(U.componentColor("&6&l"+songName));
            meta.lore(loreList);

            U.setPersistentDataContainerValue(meta, "musicFrontInternal", "plays_"+songName);
            item.setItemMeta(meta);

            inv.setItem(slot, item);
            slot++;
        }
        for(int i=27;i<36;i++){
            inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        if(page>1){ // get back page
            ItemStack head = ItemConfig.loadItem("music_saves_previouspage", ymlItem);
            SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowLeft"));

            U.setPersistentDataContainerValue(skullMeta, "musicFrontInternal", "page_"+(page-1));
            head.setItemMeta(skullMeta);

            inv.setItem(30, head);
        }
        if(nextPage){
            ItemStack head = ItemConfig.loadItem("music_saves_nextpage", ymlItem);
            SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowRight"));

            U.setPersistentDataContainerValue(skullMeta, "musicFrontInternal", "page_"+(page+1));
            head.setItemMeta(skullMeta);

            inv.setItem(32, head);
        }
        ItemStack goBackMenu = getGoBackArrow();
        inv.setItem(27, goBackMenu);

        ItemStack stopMusic = ItemConfig.loadItem("music_saves_stopmusic", ymlItem);
        inv.setItem(35, stopMusic);

        p.openInventory(inv);
        return inv;
    }
    private static Inventory loadSettingsInventory(Player p){
        Inventory inv = createInventory(p, "&6Reproductor General", 9);

        ItemStack goBackHead = getGoBackArrow();
        inv.setItem(0, goBackHead);

        ItemStack volItem = ItemConfig.loadItem("music_options_volume", DottUtils.ymlInternalItems);
        ItemMeta volMeta = volItem.getItemMeta();
        List<Component> volLore = volMeta.lore();
        List<Component> overRideVolLore;
        if(volLore==null){
            overRideVolLore=new ArrayList<>();
        }else{
            overRideVolLore=new ArrayList<>(volLore);
        }
        overRideVolLore.add(U.componentColor("&eVol: &f"+U.truncar(MusicConfig.getVolume(), 2)));
        volMeta.lore(overRideVolLore);
        volItem.setItemMeta(volMeta);

        ItemStack reloadItem = ItemConfig.getInternalItem("music_options_reload", Material.WRITABLE_BOOK,
                "&e&lRecargar la config", null, "musicFrontInternal", "options_reload");

        inv.setItem(3, volItem);
        inv.setItem(7, reloadItem);

        p.openInventory(inv);
        return inv;
    }
    //
    private static Inventory createInventory(Player player, String t, int size){
        Component title = U.componentColor(musicPrefix.replace(" ", "")+" "+t);
        return Bukkit.createInventory(player, size, title);
    }
    //
    public static void onInvClick(InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player p)) return;
        if(!p.hasPermission("DottUtils.music")) return;
        //U.mensajeDebug("MusicFront.onInv", p);
        InventoryView invView = event.getView();

        String title = musicPrefix.replace(" ", "");
        Component invTitleComponent = invView.title();
        String[] invTitle = U.componentToStringMsg(invTitleComponent).split(" ", 2);

        if(!(title.equals(invTitle[0]))){
            //U.mensajeDebug("no conciden titles "+ Arrays.toString(invTitle) +" | "+title, p);
            return;
        }

        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if(item==null) return;
        //
        String itemType = U.getPersistentDataContainerValue(item.getItemMeta(), "musicFrontInternal");
        if(itemType==null) return;

        ClickType clickType = event.getClick();
        //pages
        if(itemType.startsWith("page")){
            String[] output = itemType.split("_");
            loadSavesInventory(p, Integer.parseInt(output[1]));
            return;
        }
        if(itemType.startsWith("plays")){
            String[] output = itemType.split("_", 2);
            switchPlayType(p, output[1], clickType);
            return;
        }

        // gui things
        switch (itemType){
            case("menu_saves")-> loadSavesInventory(p, 1);
            case("menu_options")-> loadSettingsInventory(p);
            //
            case"go_back"-> openMenu(p);
            case "options_volume"->{
                switchOptionMusic(clickType);
                loadSettingsInventory(p);
            }
            case "options_reload"-> U.targetCommand(p, "du reload");
            case "saves_stopmusic"->switchStopMusic(p, clickType);

            //
            default -> {
                U.mensajeConsola("&citemType al clickear en un menu de musica está raro! "+itemType);
            }
        }
    }

    private static void switchPlayType(Player p, String songName, ClickType clickType){
        switch(clickType){
            case ClickType.RIGHT->{
                U.targetCommand(p, "du music play "+songName+" "+p.getName()+" true");
            }
            case ClickType.LEFT->{
                U.targetCommand(p, "du music play "+songName+" "+p.getName()+" false");
            }
            case ClickType.SHIFT_RIGHT->{
                U.targetCommand(p, "du music play "+songName+" @a true");
            }
            case ClickType.SHIFT_LEFT->{
                U.targetCommand(p, "du music play "+songName+" @a false");
            }
            default->{
               senderMessageMPr(p, "&eAcción no adecuada!");
            }
        }
    }
    private static void switchOptionMusic(ClickType clickType){
        switch (clickType){
            case RIGHT, SHIFT_RIGHT -> MusicConfig.addVolume(0.05f);
            case LEFT, SHIFT_LEFT -> MusicConfig.addVolume(-0.05f);
        }
    }
    private static void switchStopMusic(Player p, ClickType clickType){
        switch (clickType){
            case RIGHT, LEFT -> U.targetCommand(p, "du music stop");
            case SHIFT_RIGHT, SHIFT_LEFT -> U.targetCommand(p, "du music stop @a");
        }
    }
    //
    private static ItemStack getGoBackArrow(){
        ItemStack head = ItemConfig.loadItem("music_gotomenu", ymlItem);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowLeft"));

        U.setPersistentDataContainerValue(skullMeta, "musicFrontInternal", "go_back");
        head.setItemMeta(skullMeta);
        return head;
    }
}
