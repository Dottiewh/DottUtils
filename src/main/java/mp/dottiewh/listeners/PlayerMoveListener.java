package mp.dottiewh.listeners;

import mp.dottiewh.DottUtils;
import mp.dottiewh.noaliasCommands.backcore.BackUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (DottUtils.ymlConfig.getConfig().getBoolean("back_active")){ //Si está en true, sigue, si no no
            BackUtils.movementManagement(event, player);
        }

    }
}
