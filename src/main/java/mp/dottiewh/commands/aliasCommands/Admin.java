package mp.dottiewh.commands.aliasCommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.commands.Commands;
import mp.dottiewh.config.Config;

import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class Admin extends Commands {
    String errorMsg = "&cNo has usado un término correcto.\n&6Posibles usos: &eadd, remove, list";
    String type;
    String name;

    public Admin(CommandContext<CommandSourceStack> ctx, String type, String name) {
        super(ctx);
        this.type=type;
        this.name=name;

        run();
    }


    @Override
    protected void run(){ //check of what its meaning

        switch (type){
            case "add"-> add();
            case "remove" -> remove();
            case "list" -> list();

            default -> senderMessage(errorMsg);
        }

    }

    private void add(){
        Config.addAdmin(name);
        senderMessage("&aHas añadido a &f"+name+"&a a la lista de &9Admins&a!");
    }
    private  void remove(){
        Config.removeAdmin(name);
        senderMessage("&cHas removido a &f"+name+"&c de la lista de &9Admins&a!");
    }
    private void list(){
        List<String> admins = Config.getAdminList();
        String listaAdminsJoin = String.join(", ", admins);
        senderMessage("&aLista de admins: &f"+listaAdminsJoin);
    }

    //----------------------------------
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("admin")
                .then(literal("add")
                        .then(io.papermc.paper.command.brigadier.Commands.argument("playername", StringArgumentType.word())
                                .executes(ctx -> {
                                    String pName = ctx.getArgument("playername", String.class);
                                    new Admin(ctx, "add", pName);
                                    return 1;
                                })
                        )
                )
                .then(literal("remove")
                        .then(io.papermc.paper.command.brigadier.Commands.argument("playername", StringArgumentType.word())
                                .executes(ctx -> {
                                    String pName = ctx.getArgument("playername", String.class);
                                    new Admin(ctx, "remove", pName);
                                    return 1;
                                })
                        )
                )
                .then(literal("list")
                        .executes(ctx->{
                            new Admin(ctx, "list", null);
                            return 1;
                        })
                );
    }
}
