package mp.dottiewh.noaliasCommands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import mp.dottiewh.Commands;
import mp.dottiewh.ReferibleCommand;
import mp.dottiewh.utils.U;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class Gm extends ReferibleCommand {
    int input;

    public Gm(CommandContext<CommandSourceStack> ctx, CommandSender sender, int input) {
        super(ctx, sender);
        if(isListEmpty) return;
        this.input=input;
        run();
    }

    public Gm(CommandContext<CommandSourceStack> ctx, List<Player> playerList, int input) {
        super(ctx, playerList);
        if(isListEmpty) return;
        this.input=input;
        run();
    }

    @Override
    protected void run(){
        String errorMsg = "&c&lNo has introducido un valor valido.\n&6&lPosibles valores: &e0, 1, 2, 3 &8| &e&oNombre de un jugador";
        String sucMsg = "&aHas cambiado tu modo de juego a: &f";
        boolean somethingFailed=false;

        for(Player player : playerList){
            GameMode oldGameMode = player.getGameMode();
            String outputFormat = "";
            switch (input) {
                case 0 -> {
                    player.setGameMode(GameMode.SURVIVAL);
                    outputFormat="Supervivencia";
                }
                case 1 -> {
                    player.setGameMode(GameMode.CREATIVE);
                    outputFormat="Creativo";
                }
                case 2 -> {
                    player.setGameMode(GameMode.ADVENTURE);
                    outputFormat="Aventura";
                }
                case 3 -> {
                    player.setGameMode(GameMode.SPECTATOR);
                    outputFormat="Espectador";
                }

                default -> {
                    somethingFailed = true;
                }
            }
            if(somethingFailed) break;

            if(!(oldGameMode.equals(player.getGameMode()))){
                player.sendMessage(U.mensajeConPrefix(sucMsg+outputFormat));
            }
        }
        if(somethingFailed){
            senderMessage(errorMsg);
            return;
        }

        senderMessage("&a&lHas cambiado el modo de juego a: &f"+getOutput("&f")+" &8| &e(Input: "+input+")");
    }

    //
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("gm")
                .requires(ctx -> ctx.getSender().hasPermission("DottUtils.gm"))

                .then(io.papermc.paper.command.brigadier.Commands.argument("input", IntegerArgumentType.integer(0, 3))
                        .then(io.papermc.paper.command.brigadier.Commands.argument("players", ArgumentTypes.players())
                                .executes(ctx->{
                                    int input = ctx.getArgument("input", Integer.class);
                                    new Gm(ctx, getPlayerListFromCtx(ctx), input);
                                    return 1;
                                })
                        )
                        .executes(ctx->{
                            int input = ctx.getArgument("input", Integer.class);
                            new Gm(ctx, ctx.getSource().getSender(), input);
                            return 1;
                        })

                )
                //
                ;
    }
}
