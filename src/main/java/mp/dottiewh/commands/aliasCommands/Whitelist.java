package mp.dottiewh.commands.aliasCommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.commands.Commands;
import mp.dottiewh.utils.U;
import mp.dottiewh.config.Config;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class Whitelist extends Commands {
    private static String errorMsg = "&cNo has usado un término correcto.\n&6Posibles usos: &eadd, remove, list, toggle, status";
    String nameInput;
    String type;

    public Whitelist(CommandContext<CommandSourceStack> ctx, String type, String name) {
        super(ctx);
        this.nameInput=name;

        this.type=type;
        run();
    }
    public Whitelist(CommandContext<CommandSourceStack> ctx, String type) {
        super(ctx);

        this.type=type;
        run();
    }

    @Override
    protected void run(){ //check of what its meaning
        switch (type){
            case "add"-> add();
            case "remove" -> remove();
            case "list" -> list();
            case "toggle" -> toggle();
            case "status" -> status();

            default -> senderMessage(errorMsg);
        }

    }

    private void status(){
        String format = (Config.getWhiteListStatus()) ? "&aACTIVADA" : "&cDESACTIVADA";
        senderMessage("&9La whitelist está: "+format);
    }
    private void toggle(){
        boolean status = Config.getWhiteListStatus();

        if (status) { // si es true, desactiva la whitelist
            Config.offWhitelist();
            senderMessage("&9&lWhitelist &cDESACTIVADA&9&l.");
        }
        else {// si es false la activa
            Config.onWhitelist();
            senderMessage("&9&lWhitelist &aACTIVADA&9&l.");
        }
    }

    private void add(){
        if (!checkOfUser()) return;

        Config.addWhitelist(nameInput);
        senderMessage("&aHas añadido a &f"+nameInput+"&a a la lista de &fBlanca&a!");
    }
    private  void remove(){
        if (!checkOfUser()) return;

        Config.removeWhitelist(nameInput);
        senderMessage("&cHas removido a &f"+nameInput+"&c de la lista de &fBlanca&c!");
    }
    private void list(){
        List<String> Listablancos = Config.getWhitelist();
        String blancos = String.join(", ", Listablancos);
        senderMessage("&9Lista de whitelisteados: &f"+blancos);
    }

    //-------------------------
    private boolean checkOfUser(){
        if (args.length<3){
            senderMessage("&cPor favor añade un nombre.");
            return false;
        }

        this.nameInput=args[2];
        return true;
    }

    public static void checkWhitelist(AsyncPlayerPreLoginEvent event){
        String name = event.getName();

        if (!Config.getWhiteListStatus()) return; // return if off.
        if (Config.containsAdmin(name)) return;
        if (Config.containsWhitelist(name)) return;

        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, U.mensajeConColor("&cNo estás whitelisteado!"));
    }

    //----------------------------------
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("whitelist")
                .then(literal("add")
                        .then(io.papermc.paper.command.brigadier.Commands.argument("name", StringArgumentType.word())
                                .executes(ctx -> {
                                    String to = ctx.getArgument("name", String.class);
                                    new Whitelist(ctx, "add", to);
                                    return 1;
                                })
                        )
                )
                .then(literal("remove")
                        .then(io.papermc.paper.command.brigadier.Commands.argument("name", StringArgumentType.word())
                                .executes(ctx -> {
                                    String to = ctx.getArgument("name", String.class);
                                    new Whitelist(ctx, "remove", to);
                                    return 1;
                                })
                        )
                )
                //
                .then(literal("list")
                        .executes(ctx -> {
                            new Whitelist(ctx, "list");
                            return 1;
                        })
                )
                .then(literal("toggle")
                        .executes(ctx -> {
                            new Whitelist(ctx, "toggle");
                            return 1;
                        })
                )
                .then(literal("status")
                        .executes(ctx -> {
                            new Whitelist(ctx, "status");
                            return 1;
                        })
                );
    }
}
