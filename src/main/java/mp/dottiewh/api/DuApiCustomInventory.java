package mp.dottiewh.api;

import mp.dottiewh.music.front.MusicFront;
import mp.dottiewh.utils.inventorys.CustomInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

public class DuApiCustomInventory extends CustomInventory {
    public DuApiCustomInventory(Component prefixAndTitle, int size) {
        super(prefixAndTitle, size);
    }

    public DuApiCustomInventory(String prefix, String title, int size) {
        super(prefix, title, size);
    }


    //---
    /**
     * @param inv slots >= 27
     * @param page >= 1
     * @param loreForSongs El lore que ocupará cada item
     * @param dataPKey Pondrá un data_persistent_key al meta de cada item del tipo dataPKey+songName.
     *                 Por ejemplo dataPKey = plays_ | plays_test
     * @return ¡El inventario introducido, pero este YA está cambiado!
     */
    public static CustomInventory addMusicSaves(@NotNull CustomInventory inv, int page, @Nullable List<Component> loreForSongs, @Nullable String dataPKey){
        return MusicFront.addMusicSaves(inv, page, loreForSongs, dataPKey, null);
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
        return MusicFront.addMusicSaves(inv, page, loreForSongs, null, stringConsumer);
    }
    public static CustomInventory addMusicSaves(@NotNull CustomInventory inv, int page, @Nullable List<Component> loreForSongs, @Nullable String dataPKey, @Nullable BiConsumer<InventoryClickEvent, String> stringConsumer){
        return MusicFront.addMusicSaves(inv, page, loreForSongs, dataPKey, stringConsumer);
    }
}
