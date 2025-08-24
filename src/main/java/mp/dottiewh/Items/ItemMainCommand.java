package mp.dottiewh.Items;

import mp.dottiewh.Commands;
import mp.dottiewh.Items.Exceptions.InvalidItemConfigException;
import mp.dottiewh.Items.Exceptions.InvalidMaterialException;
import mp.dottiewh.Items.Exceptions.MissingMaterialException;
import mp.dottiewh.Utils.U;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public class ItemMainCommand extends Commands {
    private static String prefix;


    public ItemMainCommand(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);
        prefix = U.getMsgPath("item_prefix");
        run();
    }

    @Override
    protected void run() {
        String errorMsg = "&cNo has usado bien el comando.\n&6Posibles usos: &esave, get, delete &e&o[del]&e";
        //Check
        if (args.length<2){
            senderMessageIPr(errorMsg);
            return;
        }
        if (args.length<3){
            senderMessageIPr("&cPorfavor, añade el nombre de algún item.");
            return;
        }

        switch (args[1]){
            case "save" -> save(args[2]);
            case "get" -> get(args[2]);
            case "delete", "del" -> del(args[2]);

            default -> senderMessageIPr(errorMsg);
        }
    }
    private void del(String name){
        try {
            ItemConfig.removeItem(name);
        }catch (InvalidItemConfigException e){
            senderMessageIPr("&cTu item posiblemente no existe, más detalles en consola.");
            U.STmensajeConsolaNP(e.toString());
            return;
        }

        senderMessageIPr("&eSe ha borrado tu item &f"+name+"&e correctamente.");
    }
    private void save(String name){
        if (!(sender instanceof Player player)){
            senderMessageIPr("&cEste comando solo lo puede usar un jugador.");
            return;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemConfig.saveItem(name, item);
        senderMessageIPr("&aHas guardado exitosamente tu item &f"+name+"!");
    }
    private void get(String name){
        if (!(sender instanceof Player player)){
            senderMessageIPr("&cNo eres un jugador!");
            return;
        }
        ItemStack item;
        try{
            item = ItemConfig.loadItem(name);
        }catch (InvalidMaterialException e){
            senderMessageIPr("&cEl material registrado de tal item es invalido. (Check console)");
            U.STmensajeConsola(e.toString());
            return;
        }catch (MissingMaterialException e){
            senderMessageIPr("&cNo hay ningún material registrado en tal item. (Check console)");
            U.STmensajeConsola(e.toString());
            return;
        }catch (InvalidItemConfigException e){
            senderMessageIPr("&cError en tu config. (Check console)");
            senderMessageIPr("&e&o(Posiblemente no exista tu item)");
            U.STmensajeConsola(e.toString());
            return;
        }
        Map<Integer, ItemStack> sobrante = player.getInventory().addItem(item);

        if(sobrante.isEmpty()){
            playerMessageIPr(player, "&aHas recibido tu item &f"+name+"&a!");
        }else{
            for (ItemStack left : sobrante.values()){
                player.getWorld().dropItemNaturally(player.getLocation(), left);
            }
            playerMessageIPr(player, "&eNo has podido recibir tu &f"+name+"&e, pero ha sido dropeado.");
        }
    }
    //--------------
    private void senderMessageIPr(String msg){
        msg = prefix+msg;
        Component message = LegacyComponentSerializer.legacy('&').deserialize(msg);
        sender.sendMessage(message);
    }
    private void playerMessageIPr(Player player, String msg){
        msg = prefix+msg;
        Component message = LegacyComponentSerializer.legacy('&').deserialize(msg);
        player.sendMessage(message);
    }
}
