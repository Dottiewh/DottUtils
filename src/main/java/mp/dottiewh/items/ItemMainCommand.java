package mp.dottiewh.items;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.Commands;
import mp.dottiewh.items.Exceptions.InvalidItemConfigException;
import mp.dottiewh.items.Exceptions.InvalidMaterialException;
import mp.dottiewh.items.Exceptions.ItemSectionEmpty;
import mp.dottiewh.items.Exceptions.MissingMaterialException;
import mp.dottiewh.utils.U;
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
    String prefix = U.getMsgPath("item_prefix");
    int max = U.getIntConfigPath("max_itemgive_amount");
    String type;
    int amount;
    String iName;

    public ItemMainCommand(CommandContext<CommandSourceStack> ctx, String type, boolean forOther) {
        super(ctx, forOther);
        this.type=type;
        run();
        if(this.allGood){
            run();
        }
    }
    public ItemMainCommand(CommandContext<CommandSourceStack> ctx, String type, boolean forOther, int amount) {
        super(ctx, forOther);
        this.type=type;
        this.amount=amount;
        if(this.allGood){
            run();
        }
    }
    public ItemMainCommand(CommandContext<CommandSourceStack> ctx, String type, boolean forOther, int amount, String iName) {
        super(ctx, forOther);
        this.type=type;
        this.amount=amount;
        this.iName=iName;
        if(this.allGood){
            run();
        }
    }

    @Override
    protected void run() {
        String errorMsg = "&cNo has usado bien el comando.\n&6Posibles usos: &esave, get, give, delete &e&o[del]&e, list";

        switch (type){
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
        String name = getItemName();

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
        String name = getItemName();

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

        String name =getItemName();

        try{
            amount=this.amount;
        } catch (Exception e) {
            senderMessage("&cCantidad incorrecta!");
            return;
        }

        if (!(sender instanceof Player player)) {
            senderMessageIPr("&cNo eres un jugador!");
            return;
        }

        coreGet(player, name, amount, false);
    }
    private void give() {
        int amount = 1;

        String name = getItemName();

        Player player;

        try{
            amount = this.amount;
        }catch (Exception e){
            senderMessageIPr("&cNo has añadido una cantidad correcta.");
            return;
        }

        player=this.target;

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
    // /du[0] item[1] get[2] nombre[3]
    private String getItemName(){
        return iName;
    }
    private boolean checkArgL4(){
        return args.length >= 4;
    }
    private boolean checkArgL5(){
        return args.length >= 5;
    }
}
