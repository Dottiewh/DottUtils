package mp.dottiewh.listeners.player;

import mp.dottiewh.utils.ItemUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class PlayerFishListener implements Listener {
    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        ItemUtils.checkItemFishCustomData(event);
    }
}
