package mp.dottiewh.Items;

import mp.dottiewh.Commands;
import mp.dottiewh.Items.Exceptions.InvalidItemConfigException;
import mp.dottiewh.Items.Exceptions.InvalidMaterialException;
import mp.dottiewh.Items.Exceptions.ItemSectionEmpty;
import mp.dottiewh.Items.Exceptions.MissingMaterialException;
import mp.dottiewh.Utils.U;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public class ItemMainCommand extends Commands {
    private static String prefix;
    private static int max;

    public ItemMainCommand(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);
        prefix = U.getMsgPath("item_prefix");
        max = U.getIntConfigPath("max_itemgive_amount");
        run();
    }

    @Override
    protected void run() {
        String errorMsg = "&cNo has usado bien el comando.\n&6Posibles usos: &esave, get, give, delete &e&o[del]&e, list";
        //Check
        if (args.length<2){
            senderMessageIPr(errorMsg);
            return;
        }

        switch (args[1]){
            case "save" -> save();
            case "get" -> get();
            case "delete", "del", "remove" -> del();
            case "list" -> list();
            case "give" -> give();

            default -> senderMessageIPr(errorMsg);
        }
    }
    private void list(){
        Set<String> items;
        try{
            items = ItemConfig.getItems();
        }catch (ItemSectionEmpty e){
            senderMessageIPr("Hay un error en tu yml, consola para detalles.");
            U.mensajeConsola(e.toString());
            return;
        }

        String itemList = String.join("&7, &f", items);
        senderMessageIPr("&aLista de items registrados: &f"+itemList);
    }
    private void del(){
        if (argNombreCheck()) return;
        String name = args[2];

        try {
            ItemConfig.removeItem(name);
        }catch (InvalidItemConfigException e){
            senderMessageIPr("&cTu item posiblemente no existe, más detalles en consola.");
            U.mensajeConsola(e.toString());
            return;
        }

        senderMessageIPr("&eSe ha borrado tu item &f"+name+"&e correctamente.");
    }
    private void save(){
        if (argNombreCheck()) return;
        String name = args[2];

        if (!(sender instanceof Player player)){
            senderMessageIPr("&cEste comando solo lo puede usar un jugador.");
            return;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemConfig.saveItem(name, item);
        senderMessageIPr("&aHas guardado exitosamente tu item &f"+name+"!");
    }
    private void get() {
        int amount = 1;

        if (argNombreCheck()) return;
        String name = args[2];


        if (argNombreCheck()) return;
        boolean amountMode = checkArgL4();

        if (amountMode){
            try{
                amount = Integer.parseInt(args[3]);
            }catch (Exception e){
                senderMessageIPr("&cNo has añadido una cantidad correcta.");
                return;
            }
        }

        if (!(sender instanceof Player player)) {
            senderMessageIPr("&cNo eres un jugador!");
            return;
        }

        coreGet(player, name, amount, false);
    }
    private void give() {
        int amount = 1;

        if (argNombreCheck()) return;
        String name = args[2];


        if (argNombreCheck()) return;
        boolean toOther = checkArgL4();
        Player player;

        boolean amountMode = checkArgL5();
        if (amountMode){
            try{
                amount = Integer.parseInt(args[4]);
            }catch (Exception e){
                senderMessageIPr("&cNo has añadido una cantidad correcta.");
                return;
            }
        }

        if (!toOther) {
            senderMessageIPr("&cAñade el nombre de un jugador!");
            return;
        }
        else{
            player = Bukkit.getPlayer(args[3]);
            if (player == null) {
                senderMessageIPr("&cEl jugador &f" + args[3] + "&c no está conectado.");
                return;
            }
        }

        coreGet(player, name, amount, true);
    }

    //--------------
    private void coreGet(Player player, String name, int amount, boolean isConsole){
        if (amount>max){
            senderMessageIPr("&cHas sobrepasado el limite definido en config. &e("+max+")");
            return;
        }

        ItemStack item;
        try{
            item = ItemConfig.loadItem(name);
        }catch (InvalidMaterialException e){
            senderMessageIPr("&cEl material registrado de tal item es invalido. (Check console)");
            U.mensajeConsola(e.toString());
            return;
        }catch (MissingMaterialException e){
            senderMessageIPr("&cNo hay ningún material registrado en tal item. (Check console)");
            U.mensajeConsola(e.toString());
            return;
        }catch (InvalidItemConfigException e){
            senderMessageIPr("&cError en tu config. (Check console)");
            senderMessageIPr("&e&o(Posiblemente no exista tu item)");
            U.mensajeConsola(e.toString());
            return;
        }


        boolean successMsg = false, fullMsg = false;
        int count = amount;
        for (int i=0; i<amount; i++) {
            Map<Integer, ItemStack> sobrante = player.getInventory().addItem(item);
            if (sobrante.isEmpty()) {
                if (!successMsg){
                    playerMessageIPr(player, "&aHas recibido tu item &f" + name + "&a! &e(x"+amount+")");
                    successMsg = true;
                }
            } else {
                for (ItemStack left : sobrante.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), left);
                }
                if (!fullMsg) {
                    playerMessageIPr(player, "&eNo has podido recibir tu &f" + name + "&e, pero ha sido dropeado. &c(x"+count+")");
                    fullMsg = true;
                }
            }
            count--;
        }

        if (isConsole){
            senderMessageIPr("&aLe has dado un item &f"+name+" &aal jugador &f"+player.getName()+" &acorrectamente! &e(x"+amount+")");
        }
    }



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
    private boolean argNombreCheck(){
        if (args.length<3){
            senderMessageIPr("&cPorfavor, añade el nombre de algún item.");
            return true;
        }
        else return false;
    }
    private boolean checkArgL4(){
        return args.length >= 4;
    }
    private boolean checkArgL5(){
        return args.length >= 5;
    }
}
