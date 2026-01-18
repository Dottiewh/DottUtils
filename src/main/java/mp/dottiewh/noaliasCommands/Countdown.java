package mp.dottiewh.noaliasCommands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import mp.dottiewh.Commands;
import mp.dottiewh.utils.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class Countdown extends Commands {
    int segundos;

    public Countdown(CommandSender sender, String stringInput, int segundos) {
        super(sender, stringInput);
        this.segundos = segundos;

        run();
    }

    @Override
    protected void run() {
        switch(stringInput){
            case("start")-> startCountdown();
            case("stop") -> runStop();
            default ->{
                senderMessage("&4&lNo se ha detectado ningún tipo de subcomando en countdown?");
                senderMessageNP("&c&l"+stringInput);
            }
        }
    }

    private void startCountdown(){
        if(segundos<0){
            senderMessageNP("&cTus segundos son menores a 0? &8&l| &7"+segundos);
            return;
        }

        U.countdownForAll(plugin, segundos, "&7Segundos restantes: &f");
        senderMessageNP("&aSe ha emitido la orden correctamente! &e("+segundos+")");
    }
    private void runStop(){
        senderMessageNP("&aSe han parado cualquier cuenta atrás existente.");
        U.stopAllCountdowns();
    }

    //
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("countdown")
                .requires(ctx -> ctx.getSender().hasPermission("DottUtils.countdown"))
                .then(literal("start")
                        .then(io.papermc.paper.command.brigadier.Commands.argument("seconds", IntegerArgumentType.integer(0))
                                .executes(ctx->{
                                    int seconds = ctx.getArgument("seconds", Integer.class);
                                    new Countdown(ctx.getSource().getSender(), "start", seconds);
                                    return 1;
                                })
                        )
                )
                .then(literal("stop")
                        .executes(ctx->{
                            new Countdown(ctx.getSource().getSender(), "stop", -1);
                            return 1;
                        })
                )
                //
                ;
    }
}
