package mp.dottiewh.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import mp.dottiewh.DottUtils;
import mp.dottiewh.cinematics.CinematicMainCommand;
import mp.dottiewh.commands.aliasCommands.*;
import mp.dottiewh.commands.noaliasCommands.*;
import mp.dottiewh.items.ItemMainCommand;
import mp.dottiewh.music.MusicMainCommand;
import mp.dottiewh.commands.noaliasCommands.backcore.BackCommand;
import mp.dottiewh.commands.noaliasCommands.playtimecore.PlayTime;
import mp.dottiewh.commands.noaliasCommands.tpacore.Tpa;
import mp.dottiewh.commands.noaliasCommands.tpacore.TpaAccept;
import mp.dottiewh.commands.noaliasCommands.tpacore.TpaCancel;
import mp.dottiewh.commands.noaliasCommands.tpacore.TpaDeny;
import mp.dottiewh.utils.U;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class Commands {
    private static final Logger log = LoggerFactory.getLogger(Commands.class);
    protected Set<String> comandosRegistrados;
    protected Command command;
    protected String label;
    protected String[] args;
    //
    protected CommandSender sender;
    protected String stringInput;
    protected Plugin plugin;
    protected Player classTarget;
    protected boolean allGood;

    @Deprecated(forRemoval = true, since = "1.2.2")
    protected Commands (Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        this.sender = sender;
        this.command = command;
        this.label = label;
        this.args = args;
        this.comandosRegistrados = comandosRegistrados;
        this.plugin = JavaPlugin.getProvidingPlugin(getClass());
    }
    protected Commands(CommandSender sender, String stringInput){
        this.sender=sender;
        this.stringInput = stringInput;
        this.plugin= DottUtils.getPlugin();
    }
    protected Commands(CommandContext<CommandSourceStack> ctx, boolean target){
        this.sender=ctx.getSource().getSender();
        this.stringInput =ctx.getInput();
        this.plugin=DottUtils.getPlugin();
        this.allGood=true;
        if(target){
            Player p = getPlayerFromCtx(ctx);
            if(p==null){
                senderMessageNP("&cNo has introducido un jugador online!");
                this.allGood=false;
            }
            this.classTarget =p;
        }

    }
    protected Commands(CommandContext<CommandSourceStack> ctx){
        this.sender=ctx.getSource().getSender();
        this.stringInput =ctx.getInput();
        this.plugin=DottUtils.getPlugin();
    }

    //==================
    public static void regNoAliasCommands(Plugin plugin){
        List<LiteralArgumentBuilder<CommandSourceStack>> listaLiterals = new LinkedList<>();

        listaLiterals.add(Status.getLiteralBuilder());
        listaLiterals.add(Repair.getLiteralBuilder("repair"));
        listaLiterals.add(Repair.getLiteralBuilder("fix"));
        listaLiterals.add(Jump.getLiteralBuilder());
        listaLiterals.add(Heal.getLiteralBuilder());
        listaLiterals.add(Gm.getLiteralBuilder());
        listaLiterals.add(Fly.getLiteralBuilder());
        listaLiterals.add(Feed.getLiteralBuilder());
        listaLiterals.add(Countdown.getLiteralBuilder());
        listaLiterals.add(Coordenadas.getLiteralBuilder("coords"));
        listaLiterals.add(Coordenadas.getLiteralBuilder("coordenadas"));
        listaLiterals.add(Tpa.getLiteralBuilder());
        listaLiterals.add(TpaAccept.getLiteralBuilder());
        listaLiterals.add(TpaCancel.getLiteralBuilder());
        listaLiterals.add(TpaDeny.getLiteralBuilder());
        listaLiterals.add(PlayTime.getLiteralBuilder());
        listaLiterals.add(BackCommand.getLiteralBuilder());
        //=========
        listaLiterals.add(AdminChat.getLiteralBuilder("adminchat"));
        listaLiterals.add(AdminChat.getLiteralBuilder("achat"));
        listaLiterals.add(AdminChat.getLiteralBuilder("ac"));

        //
        for(LiteralArgumentBuilder<CommandSourceStack> litBuilder : listaLiterals){
            plugin.getLifecycleManager().registerEventHandler(
                    LifecycleEvents.COMMANDS,
                    event->{
                        event.registrar().register(
                                litBuilder.build(),
                                "No Alias DottUtils command."
                        );
                    }
            );
        }
    }
    //
    public static LiteralArgumentBuilder<CommandSourceStack> createAlias(Plugin pl, String name){
        return literal(name)
                .requires(ctx -> ctx.getSender().hasPermission("DottUtils.dottutils"))
                .then(Admin.getLiteralBuilder())
                .then(Reload.getLiteralBuilder())
                .then(Help.getLiteralBuilder())
                //
                .then(AdminChat.getLiteralBuilder("adminchat"))
                .then(AdminChat.getLiteralBuilder("achat"))
                .then(AdminChat.getLiteralBuilder("ac"))
                //
                .then(Whitelist.getLiteralBuilder())
                .then(Pvp.getLiteralBuilder())
                .then(NoFall.getLiteralBuilder())

                .then(TellRaw.getLiteralBuilder())
                //
                .then(ItemMainCommand.getLiteralBuilder())
                //
                .then(MusicMainCommand.getLiteralBuilder())
                //
                .then(CinematicMainCommand.getLiteralBuilder())
                //-------
                ;
    }

    protected static Player getPlayerFromCtx(CommandContext<CommandSourceStack> ctx){
        PlayerSelectorArgumentResolver resolver =
                ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
        List<Player> players;
        try{
            players = resolver.resolve(ctx.getSource());
        }catch(CommandSyntaxException e){
            U.mensajeConsolaNP("&cExcepción de syntax. Track: &4"+ Arrays.toString(e.getStackTrace()));
            return null;
        }

        if (players.isEmpty()) {
            ctx.getSource().getSender().sendMessage("Jugador no encontrado.");
            return null;
        }

        return players.getFirst();
    }
    protected static List<Player> getPlayerListFromCtx(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        PlayerSelectorArgumentResolver resolver = ctx.getArgument("players", PlayerSelectorArgumentResolver.class);
        return resolver.resolve(ctx.getSource());
    }

    // comandos como tal
    protected void run(){
        U.mensajeConsola("&4&lrun no definido.");
    };


    // metodos utiles
    protected Player checkIfForOtherPlayerP(String name, Player defaultP){
        boolean b = checkIfForOtherPlayer(name);
        if (b) return Bukkit.getPlayerExact(name);
        else return defaultP;
    }
    protected boolean checkIfForOtherPlayer(String name){
        if(name==null){
            senderMessageNP("&4No se ha reconocido al jugador.");
            return false;
        }

        Player player = Bukkit.getPlayerExact(name);
        if(player==null){
            senderMessageNP("&8&l> &cEl jugador &e"+name+"&c no está conectado.");
            return false;
        }

        return true;
    }
    protected void senderMessage(String mensaje){
        sender.sendMessage(U.mensajeConPrefix(mensaje));
    }
    protected void senderMessageNP(String mensaje){
        sender.sendMessage(U.mensajeConColor(mensaje));
    }
}
