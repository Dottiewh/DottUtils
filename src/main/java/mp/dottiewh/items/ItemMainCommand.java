package mp.dottiewh.items;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import mp.dottiewh.commands.Commands;
import mp.dottiewh.commands.ReferibleCommand;
import mp.dottiewh.items.exceptions.*;
import mp.dottiewh.utils.U;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.C;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class ItemMainCommand extends ReferibleCommand {
    String prefix = U.getMsgPath("item_prefix");
    int max = U.getIntConfigPath("max_itemgive_amount");
    String type;
    int amount;
    String iName;

    public ItemMainCommand(CommandContext<CommandSourceStack> ctx, String type, boolean forOther) {
        super(ctx, forOther);
        this.type=type;
        run();
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
    public ItemMainCommand(CommandContext<CommandSourceStack> ctx, String type, int amount, String iName, List<Player> pList) {
        super(ctx, pList);
        this.type=type;
        this.amount=amount;
        this.iName=iName;

        run();
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
        debugMsg("ItemMainCommand.list");
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
        debugMsg("ItemMainCommand.del");
        String name = getItemName();
        String fileName = null;
        if(name.contains(".")){
            String[] nameArray = name.split("\\.", 2);
            if(nameArray[1].contains(".")){
                senderMessageIPr("&cEl nombre del item no debería tener un '&4.&c'! ("+nameArray[1]+")");
                senderMessageIPr("&cSi estás muy seguro de que existe borralo manualmente.");
                return;
            }
            if(nameArray[1].isEmpty()){
                senderMessageIPr("&cIntroduce un id valido para borrar tu item!");
                return;
            }
            name=nameArray[1];
            fileName=nameArray[0];
        }

        try {
            ItemConfig.removeItem(name, fileName);
        }catch (InvalidItemConfigException e){
            senderMessageIPr("&cTu item posiblemente no existe, más detalles en consola.");
            U.mensajeConsola(e.toString());
            return;
        }

        if(fileName!=null) senderMessageIPr("&eSe ha borrado tu item &f"+name+"&e correctamente desde el archivo &6"+fileName+"&e.");
        else senderMessageIPr("&eSe ha borrado tu item &f"+name+"&e correctamente.");
    }
    private void save(){
        debugMsg("ItemMainCommand.save");
        String name = getItemName();

        if (!(sender instanceof Player player)){
            senderMessageIPr("&cEste comando solo lo puede usar un jugador.");
            return;
        }
        if(ItemConfig.existsItem(name)){
            senderMessageIPr("&cEl item &4"+name+"&c ya está registrado! Considera borrarlo primero.");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if(name.contains(".")){
            String[] nameArray = name.split("\\.", 2);
            if(nameArray[1].contains(".")){
                senderMessageIPr("&cEl id de tu item no puede contener un '&4.&c'. ("+nameArray[1]+")");
                return;
            }
            if(nameArray[1].isEmpty()){
                senderMessageIPr("&cIntroduce un id para guardar tu item!");
                return;
            }
            ItemConfig.saveItem(nameArray[1], item, nameArray[0]);
            senderMessageIPr("&aHas guardado exitosamente tu item &f"+nameArray[1]+"&a en el archivo &6"+nameArray[0]+".yml&a!");
        }else{
            ItemConfig.saveItem(name, item, null);
            senderMessageIPr("&aHas guardado exitosamente tu item &f"+name+"&a!");
        }
    }
    private void get() {
        debugMsg("ItemMainCommand.get");
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
        debugMsg("ItemMainCommand.give");
        if(isListEmpty) return;
        int amount = 1;

        String name = getItemName();

        try{
            amount = this.amount;
        }catch (Exception e){
            senderMessageIPr("&cNo has añadido una cantidad correcta.");
            return;
        }

        for(Player p : playerList){
            if(!coreGet(p, name, amount, true)){
                break;
            }
        }
    }

    //--------------
    private boolean coreGet(Player player, String name, int amount, boolean isConsole){
        debugMsg("ItemMainCommand.coreGet");
        if (amount>max){
            senderMessageIPr("&cHas sobrepasado el limite definido en config. &e("+max+")");
            return false;
        }

        ItemStack item;
        String fileName=null;
        if(name.contains(".")){
            String[] nameArray = name.split("\\.", 2);
            if(nameArray[1].contains(".")){
                senderMessageIPr("&cEl id de tu item no puede contener un '&4.&c'! ("+nameArray[1]+")");
                return false;
            }
            fileName=nameArray[0];
            name=nameArray[1];
            U.mensajeDebugConsole(Arrays.toString(nameArray));
        }

        try{
            item = ItemConfig.loadItem(name, fileName);
        }catch (InvalidMaterialException e){
            senderMessageIPr("&cEl material registrado de tal item es invalido. (Check console)");
            U.mensajeConsola(e.toString());
            return false;
        }catch (MissingMaterialException e){
            senderMessageIPr("&cNo hay ningún material registrado en tal item. (Check console)");
            U.mensajeConsola(e.toString());
            return false;
        }catch (InvalidItemFile e){
            senderMessageIPr("&cPosiblemente no existe el archivo a guardarlo! (Check console)");
            U.mensajeConsola(e.toString());
            return false;
        }
        catch (InvalidItemConfigException e){
            senderMessageIPr("&cError en tu config. (Check console)");
            senderMessageIPr("&e&o(Posiblemente no exista tu item)");
            U.mensajeConsola(e.toString());
            return false;
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
        return true;
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

    //----------------------------------
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("item")
                .then(literal("save")
                        .then(io.papermc.paper.command.brigadier.Commands.argument("itemName", StringArgumentType.word())
                                .suggests(item_suggestions_files)
                                .executes(ctx -> {
                                    String item = ctx.getArgument("itemName", String.class);
                                    new ItemMainCommand(ctx,"save", false,0 ,item);
                                    return 1;
                                })
                        )
                )
                .then(literal("get")
                        .then(io.papermc.paper.command.brigadier.Commands.argument("itemName", StringArgumentType.word())
                                .suggests(Commands.item_suggestions)
                                .executes(ctx -> {
                                    String item = ctx.getArgument("itemName", String.class);
                                    new ItemMainCommand(ctx,"get", false, 1, item);
                                    return 1;
                                })
                                .then(io.papermc.paper.command.brigadier.Commands.argument("amount", IntegerArgumentType.integer(0))
                                        .executes(ctx -> {
                                            int amount = ctx.getArgument("amount", Integer.class);
                                            String item = ctx.getArgument("itemName", String.class);
                                            new ItemMainCommand(ctx, "get", false, amount, item);
                                            return 1;
                                        })
                                )
                        )
                )
                .then(literal("remove")
                        .then(io.papermc.paper.command.brigadier.Commands.argument("itemName", StringArgumentType.word())
                                .suggests(Commands.item_suggestions)
                                .executes(ctx -> {
                                    String item = ctx.getArgument("itemName", String.class);
                                    new ItemMainCommand(ctx, "remove", false, 0, item);
                                    return 1;
                                })
                        )
                )
                .then(literal("list")
                        .executes(ctx -> {
                            new ItemMainCommand(ctx, "list", false);
                            return 1;
                        })
                )
                .then(literal("give")
                        .then(io.papermc.paper.command.brigadier.Commands.argument("itemName", StringArgumentType.word())
                                .suggests(Commands.item_suggestions)
                                .then(io.papermc.paper.command.brigadier.Commands.argument("players", ArgumentTypes.players())
                                        .executes(ctx->{
                                            String item = ctx.getArgument("itemName", String.class);
                                            new ItemMainCommand(ctx, "give", 1, item, getPlayerListFromCtx(ctx));
                                            return 1;
                                        })
                                        .then(io.papermc.paper.command.brigadier.Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(ctx -> {
                                                    String item = ctx.getArgument("itemName", String.class);
                                                    int amount = ctx.getArgument("amount", Integer.class);
                                                    new ItemMainCommand(ctx, "give", amount, item, getPlayerListFromCtx(ctx));
                                                    return 1;
                                                })
                                        )
                                )
                        )
                );
    }
}
