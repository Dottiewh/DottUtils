package mp.dottiewh.utils.inventorys;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class CustomMenu {
    protected CustomInventory mainMenu = null;
    protected final HashMap<String, CustomInventory> childs = new HashMap<>();

    public CustomMenu(CustomInventory mainMenu) {
        this.mainMenu = mainMenu;
    }

    @Nullable
    public CustomInventory getChild(String key){
        return childs.get(key);
    }
    public void addChild(String key, CustomInventory inv){
        childs.put(key ,inv);
    }

    public CustomInventory getMainMenu(){
        return mainMenu;
    }
}
