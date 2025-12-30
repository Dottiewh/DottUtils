package mp.dottiewh;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import mp.dottiewh.cinematics.CinematicMainCommand;
import mp.dottiewh.items.ItemMainCommand;
import mp.dottiewh.music.MusicMainCommand;
import mp.dottiewh.noaliasCommands.playtimecore.PlayTime;
import mp.dottiewh.noaliasCommands.tpacore.Tpa;
import mp.dottiewh.noaliasCommands.tpacore.TpaAccept;
import mp.dottiewh.noaliasCommands.tpacore.TpaCancel;
import mp.dottiewh.noaliasCommands.tpacore.TpaDeny;
import mp.dottiewh.utils.U;
import mp.dottiewh.noaliasCommands.backcore.BackCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import  mp.dottiewh.noaliasCommands.*;
import mp.dottiewh.aliasCommands.*;

import java.util.*;

import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

public abstract class Commands {
    protected Set<String> comandosRegistrados;
    protected Command command;
    protected String label;
    protected String[] args;
    //
    protected CommandSender sender;
    protected String input;
    protected Plugin plugin;
    protected Player target;

    @Deprecated
    protected Commands (Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        this.sender = sender;
        this.command = command;
        this.label = label;
        this.args = args;
        this.comandosRegistrados = comandosRegistrados;
        this.plugin = JavaPlugin.getProvidingPlugin(getClass());
    }
    protected Commands(CommandSender sender, String input){
        this.sender=sender;
        this.input=input;
        this.plugin=DottUtils.getPlugin();
    }
    protected Commands(CommandContext<CommandSourceStack> ctx, boolean target){
        this.sender=ctx.getSource().getSender();
        this.input=ctx.getInput();
        this.plugin=DottUtils.getPlugin();
        if(target){
            Player p = getPlayerFromCtx(ctx);
            if(p==null){
                senderMessageNP("&cNo has introducido un jugador online!");
                return;
            }
        }

        run();
    }

    public static void commandCore(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args){
        String cmdString = command.getName();
        if (!comandosRegistrados.contains(cmdString)){
            sender.sendMessage(U.mensajeConPrefix(U.getMsgPath("not_right_command"))); //"&c&lNo se ha encontrado tu comando."
            return;
        }


        switch (cmdString.toLowerCase()){
            case "gm"-> new Gm(comandosRegistrados, sender, command, label, args);
            case "jump" -> new Jump(comandosRegistrados, sender, command, label, args);
            case "status" -> new Status(comandosRegistrados, sender, command, label, args);
            case "back" -> new BackCommand(comandosRegistrados, sender, command, label, args);
            case "tpa" -> new Tpa(comandosRegistrados, sender, command, label, args);
            case "tpaaccept" -> new TpaAccept(comandosRegistrados, sender, command, label, args);
            case "tpacancel" -> new TpaCancel(comandosRegistrados, sender, command, label, args);
            case "tpadeny" -> new TpaDeny(comandosRegistrados, sender, command, label, args);
            case "playtime" -> new PlayTime(comandosRegistrados, sender, command, label, args);
            case "repair" -> new Repair(comandosRegistrados, sender, command, label, args);
            case "heal" -> new Heal(comandosRegistrados, sender, command, label, args);
            case "feed" -> new Feed(comandosRegistrados, sender, command, label, args);
            case "coords", "coordenadas", "coord", "antonia" -> new Coordenadas(comandosRegistrados, sender, command, label, args);
            case "countdown" -> new Countdown(comandosRegistrados, sender, command, label, args);
            case "adminchat", "ac", "achat" -> new AdminChat(comandosRegistrados, sender, command, label, args, true);
            case "dottutils", "du", "dutils" -> checkAllias(comandosRegistrados, sender, command, label, args);

            default -> sender.sendMessage(U.mensajeConPrefix(U.getMsgPath("non_registered_command"))); //"&c&lComando no registrado."
        }

//
    }
    private static void checkAllias(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args){

        if (args.length<1){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',U.getMsgPath("non_registered_command")));
            new Help(comandosRegistrados, sender, command, label, args);
            return;
        }
        String input = args[0].toLowerCase();
        switch (input){
            //case "admin", "adm" -> new Admin(comandosRegistrados, sender, command, label, args);
            case "reload" -> new Reload(comandosRegistrados, sender, command, label, args);
            case "help", "-h", "--help" -> new Help(comandosRegistrados, sender, command, label, args);
            case "adminchat", "ac" -> new AdminChat(comandosRegistrados, sender, command, label, args, false);
            case "whitelist", "wl" -> new Whitelist(comandosRegistrados, sender, command, label, args);
            case "pvp" -> new Pvp(comandosRegistrados, sender, command, label, args);
            case "nofall", "nf" -> new NoFall(comandosRegistrados, sender, command, label, args);
            case "item"-> new ItemMainCommand(comandosRegistrados, sender, command, label, args);
            case "music"-> new MusicMainCommand(comandosRegistrados, sender, command, label, args);
            case "cinematic" -> new CinematicMainCommand(comandosRegistrados, sender, command, label, args);

            default -> {
                sender.sendMessage(U.mensajeConPrefix("&c&lSub-índice no encontrado."));
                sender.sendMessage(U.mensajeConPrefix("&6Puedes probar usando &f/du help&6!"));
            }
        }
    }
    public static LiteralArgumentBuilder<CommandSourceStack> createAlias(Plugin pl, String name){
        return literal(name)
                .requires(ctx -> ctx.getSender().hasPermission("DottUtils.dottutils"))
                .then(literal("admin")
                        .then(literal("add")
                                .then(io.papermc.paper.command.brigadier.Commands.argument("player", ArgumentTypes.player())
                                        .executes(ctx -> {
                                            new Admin(ctx, "add", true);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("remove")
                                .then(io.papermc.paper.command.brigadier.Commands.argument("player", ArgumentTypes.player())
                                        .executes(ctx -> {
                                            new Admin(ctx, "remove", true);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("list")
                                .executes(ctx->{
                                    new Admin(ctx, "list", false);
                                    return 1;
                                })
                        )
                );
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


    // comandos como tal
    protected abstract void run();


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
