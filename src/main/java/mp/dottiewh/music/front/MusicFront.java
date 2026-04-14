package mp.dottiewh.music.front;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.DottUtils;
import mp.dottiewh.config.CustomConfig;
import mp.dottiewh.items.ItemConfig;
import mp.dottiewh.music.MusicConfig;
import mp.dottiewh.music.MusicMainCommand;
import mp.dottiewh.music.classes.LegacyMusic;
import mp.dottiewh.utils.U;
import mp.dottiewh.utils.inventorys.CustomInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class MusicFront extends MusicMainCommand {
    Player player;
    private static final CustomConfig ymlItem = DottUtils.ymlInternalItems;

    public static void buildFront(CommandContext<CommandSourceStack> ctx){
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
        MusicMenus.getMainInventory().createInventoryAndOpen(player);
    }


    static void switchPlayType(Player p, String songName, ClickType clickType){
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
    static void switchVolumeMusicOption(ClickType clickType){
        switch (clickType){
            case RIGHT, SHIFT_RIGHT -> MusicConfig.addVolume(0.05f);
            case LEFT, SHIFT_LEFT -> MusicConfig.addVolume(-0.05f);
        }
    }
    static void switchStopMusic(Player p, ClickType clickType){
        switch (clickType){
            case RIGHT, LEFT -> U.targetCommand(p, "du music stop");
            case SHIFT_RIGHT, SHIFT_LEFT -> U.targetCommand(p, "du music stop @a");
        }
    }
    public static String timeFormat(double segundos){
        if(segundos<60) return U.truncar(segundos, 2)+"s";

        int minutos = 0;

        while(segundos>=60){
            segundos-=60;
            minutos++;
        }

        return minutos+"m "+U.removeDecimals(segundos)+"s";
    }

    //----------------------------------------------
    /**
     * @param inv slots >= 27
     * @param page >= 1
     * @param loreForSongs El lore que ocupará cada item
     * @param dataPKey Pondrá un data_persistent_key al meta de cada item del tipo dataPKey+songName.
     *                 Por ejemplo dataPKey = plays_ | plays_test
     * @return ¡El inventario introducido, pero este YA está cambiado!
     */
    public static CustomInventory addMusicSaves(@NotNull CustomInventory inv, int page, @Nullable List<Component> loreForSongs, @Nullable String dataPKey){
        return addMusicSaves(inv, page, loreForSongs, dataPKey, null);
    }
    /**
     * @param inv slots >= 27
     * @param page >= 1
     * @param loreForSongs El lore que ocupará cada item
     * @param stringConsumer Pondrá un BiConsumer asignado a cada canción, tal que
     *                       InventoryClickEvent será el evento en sí y
     *                       el nombre de la canción será el string que alimente.
     * @return ¡El inventario introducido, pero este YA está cambiado!
     */
    public static CustomInventory addMusicSaves(@NotNull CustomInventory inv, int page, @Nullable List<Component> loreForSongs, @Nullable BiConsumer<InventoryClickEvent, String> stringConsumer){
        return addMusicSaves(inv, page, loreForSongs, null, stringConsumer);
    }
    public static CustomInventory addMusicSaves(@NotNull CustomInventory inv, int page, @Nullable List<Component> loreForSongs, @Nullable String dataPKey, @Nullable BiConsumer<InventoryClickEvent, String> biConsumer){
        int maxSize = page*27;
        List<String> musicArray = MusicConfig.getMusicList();

        int slot=0;
        for(int i=(page-1)*27;(i<maxSize)&&(i<musicArray.size());i++){
            List<Component> loreListCopy = (loreForSongs!=null) ? new ArrayList<>(loreForSongs) : new ArrayList<>();

            String songName = musicArray.get(i);
            LegacyMusic music = LegacyMusic.getFromCache(songName);
            if(music==null) continue;

            int tickDuration = music.getTicksDuration();
            String formattedDuration = MusicFront.timeFormat(tickDuration/20d);
            loreListCopy.addFirst(U.componentColor("&6Duración: &e"+tickDuration+" &7("+formattedDuration+")"));
            String titleAndAuthor = music.getTitleAndAuthor();
            if(titleAndAuthor==null) titleAndAuthor="<b><color:#bb67e6>"+songName+"</color></b>";

            loreListCopy.addFirst(U.componentColor("&7"+songName));

            ItemStack item = new ItemStack(music.getDisplayMaterial());
            ItemMeta meta = item.getItemMeta();

            meta.displayName(U.componentColorHexMini(titleAndAuthor));
            meta.lore(loreListCopy);

            if(dataPKey!=null) U.setPersistentDataContainerValue(meta, "musicFrontInternal", dataPKey+songName);

            item.setItemMeta(meta);

            inv.setItem(slot, item);
            if(biConsumer!=null){
                inv.setItemTask(slot, e-> biConsumer.accept(e, songName));
            }
            slot++;
        }
        return inv;
    }
}
