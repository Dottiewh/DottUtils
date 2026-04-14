package mp.dottiewh.api;

import mp.dottiewh.utils.inventorys.CustomInventory;
import net.kyori.adventure.text.Component;

public class DuApiCustomInventory extends CustomInventory {
    public DuApiCustomInventory(Component prefixAndTitle, int size) {
        super(prefixAndTitle, size);
    }

    public DuApiCustomInventory(String prefix, String title, int size) {
        super(prefix, title, size);
    }
}
