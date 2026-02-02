package mp.dottiewh.listeners.player;

import mp.dottiewh.DottUtils;
import mp.dottiewh.commands.noaliasCommands.backcore.BackUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){

        if (DottUtils.ymlConfig.getConfig().getBoolean("back_active")){ //Si está en true, sigue, si no no
            BackUtils.backOnDeathManagement(event);
        }
    }
}
