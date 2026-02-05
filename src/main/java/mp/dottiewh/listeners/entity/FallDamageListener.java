package mp.dottiewh.listeners.entity;


import mp.dottiewh.utils.U;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class FallDamageListener implements Listener {

    @EventHandler
    public void onFallDamage(EntityDamageEvent event){
        U.noFall(event); //checkea /du nf
        U.noFall_core(event); // solo sirve para cosas del tipo /jump
    }
}
