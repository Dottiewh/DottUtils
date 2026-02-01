package mp.dottiewh.items.classes;

import mp.dottiewh.DottUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

public class ItemCustomData {
    private static Plugin plugin = DottUtils.getPlugin();

    Particle particle=null;
    PotionEffectType effect=null;



    public ItemCustomData(PersistentDataContainer data, String key){
        String outputParticle = data.get(new NamespacedKey(plugin, key), PersistentDataType.STRING);
    }
}
