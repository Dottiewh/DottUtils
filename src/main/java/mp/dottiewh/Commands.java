package mp.dottiewh;

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
import mp.dottiewh.cinematics.CinematicMainCommand;
import mp.dottiewh.items.ItemMainCommand;
import mp.dottiewh.music.MusicMainCommand;
import mp.dottiewh.noaliasCommands.*;
import mp.dottiewh.noaliasCommands.backcore.BackCommand;
import mp.dottiewh.noaliasCommands.playtimecore.PlayTime;
import mp.dottiewh.noaliasCommands.tpacore.Tpa;
import mp.dottiewh.noaliasCommands.tpacore.TpaAccept;
import mp.dottiewh.noaliasCommands.tpacore.TpaCancel;
import mp.dottiewh.noaliasCommands.tpacore.TpaDeny;
import mp.dottiewh.utils.U;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import mp.dottiewh.aliasCommands.*;
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

    @Deprecated
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
        this.plugin=DottUtils.getPlugin();
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

    public static void commandCore(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args){
        String cmdString = command.getName();
        if (!comandosRegistrados.contains(cmdString)){
            sender.sendMessage(U.mensajeConPrefix(U.getMsgPath("not_right_command"))); //"&c&lNo se ha encontrado tu comando."
            return;
        }


        switch (cmdString.toLowerCase()){
            //case "gm"-> new Gm(comandosRegistrados, sender, command, label, args);
            //case "jump" -> new Jump(comandosRegistrados, sender, command, label, args);
            //case "status" -> new Status(comandosRegistrados, sender, command, label, args);
            //case "back" -> new BackCommand(comandosRegistrados, sender, command, label, args);
            //case "tpa" -> new Tpa(comandosRegistrados, sender, command, label, args);
            //case "tpaaccept" -> new TpaAccept(comandosRegistrados, sender, command, label, args);
            //case "tpacancel" -> new TpaCancel(comandosRegistrados, sender, command, label, args);
            //case "tpadeny" -> new TpaDeny(comandosRegistrados, sender, command, label, args);
            //case "playtime" -> new PlayTime(comandosRegistrados, sender, command, label, args);
            //case "repair" -> new Repair(comandosRegistrados, sender, command, label, args);
            //case "heal" -> new Heal(comandosRegistrados, sender, command, label, args);
            //case "feed" -> new Feed(comandosRegistrados, sender, command, label, args);
            //case "coords", "coordenadas", "coord", "antonia" -> new Coordenadas(comandosRegistrados, sender, command, label, args);
            //case "countdown" -> new Countdown(comandosRegistrados, sender, command, label, args);
            //case "adminchat", "ac", "achat" -> new AdminChat(comandosRegistrados, sender, command, label, args, true);
            case "dottutils", "du", "dutils" -> checkAllias(comandosRegistrados, sender, command, label, args);

            default -> sender.sendMessage(U.mensajeConPrefix(U.getMsgPath("non_registered_command"))); //"&c&lComando no registrado."
        }

//
    }
    private static void checkAllias(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args){

        if (args.length<1){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',U.getMsgPath("non_registered_command")));
            return;
        }
        String input = args[0].toLowerCase();
        switch (input){
            //case "admin", "adm" -> new Admin(comandosRegistrados, sender, command, label, args);
            //case "reload" -> new Reload(comandosRegistrados, sender, command, label, args);
            //case "help", "-h", "--help" -> new Help(comandosRegistrados, sender, command, label, args);
            //case "adminchat", "ac" -> new AdminChat(comandosRegistrados, sender, command, label, args, false);
            //case "whitelist", "wl" -> new Whitelist(comandosRegistrados, sender, command, label, args);
            //case "pvp" -> new Pvp(comandosRegistrados, sender, command, label, args);
            //case "nofall", "nf" -> new NoFall(comandosRegistrados, sender, command, label, args);
            //case "item"-> new ItemMainCommand(comandosRegistrados, sender, command, label, args);
            //case "music"-> new MusicMainCommand(comandosRegistrados, sender, command, label, args);
            //case "cinematic" -> new CinematicMainCommand(comandosRegistrados, sender, command, label, args);

            default -> {
                sender.sendMessage(U.mensajeConPrefix("&c&lSub-índice no encontrado."));
                sender.sendMessage(U.mensajeConPrefix("&6Puedes probar usando &f/du help&6!"));
            }
        }
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
                .then(literal("admin")
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
                        )
                )
                .then(literal("reload")
                        .executes(ctx->{
                            new Reload(ctx);
                            return 1;
                        })
                )
                .then(literal("help")
                        .executes(ctx->{
                            new Help(ctx, 1);
                            return 1;
                        })
                        .then(io.papermc.paper.command.brigadier.Commands.argument("pagina", IntegerArgumentType.integer(0))
                                .executes(ctx -> {
                                    int page = ctx.getArgument("pagina", Integer.class);
                                    new Help(ctx, page);
                                    return 1;
                                })
                        )
                )
                .then(literal("adminchat")
                        .then(literal("toggle")
                                .executes(ctx->{
                                    new AdminChat(ctx, "toggle", false);
                                    return 1;
                                })
                        )
                        .then(literal("leave")
                                .executes(ctx->{
                                    new AdminChat(ctx, "leave", false);
                                    return 1;
                                })
                        )
                        .then(literal("join")
                                .executes(ctx->{
                                    new AdminChat(ctx, "join", false);
                                    return 1;
                                })
                        )
                )
                .then(literal("whitelist")
                        .then(literal("add")
                                .then(io.papermc.paper.command.brigadier.Commands.argument("name", StringArgumentType.word())
                                        .executes(ctx -> {
                                            String to = ctx.getArgument("itemnombre", String.class);
                                            new Whitelist(ctx, "add", to);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("remove")
                                .then(io.papermc.paper.command.brigadier.Commands.argument("name", StringArgumentType.word())
                                        .executes(ctx -> {
                                            String to = ctx.getArgument("itemnombre", String.class);
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
                        )
                )
                .then(literal("pvp")
                        .then(literal("toggle")
                                .executes(ctx->{
                                    new Pvp(ctx, "toggle");
                                    return 1;
                                })
                        )
                        .then(literal("status")
                                .executes(ctx->{
                                    new Pvp(ctx, "status");
                                    return 1;
                                })
                        )
                )
                .then(literal("nofall")
                        .then(literal("toggle")
                                .executes(ctx->{
                                    new NoFall(ctx, "toggle");
                                    return 1;
                                })
                        )
                        .then(literal("status")
                                .executes(ctx->{
                                    new NoFall(ctx, "status");
                                    return 1;
                                })
                        )
                )
                .then(literal("tellraw")
                        .then(io.papermc.paper.command.brigadier.Commands.argument("players", ArgumentTypes.players())
                                .then(io.papermc.paper.command.brigadier.Commands.argument("text", StringArgumentType.greedyString())
                                        .executes(ctx->{
                                            String text = ctx.getArgument("text", String.class);
                                            new TellRaw(ctx, text, getPlayerListFromCtx(ctx));
                                            return 1;
                                        })
                                )
                        )
                )
                //
                .then(literal("item")
                        .then(literal("save")
                                .then(io.papermc.paper.command.brigadier.Commands.argument("itemName", StringArgumentType.word())
                                        .executes(ctx -> {
                                            String item = ctx.getArgument("itemName", String.class);
                                            new ItemMainCommand(ctx,"save", false,0 ,item);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("get")
                                .then(io.papermc.paper.command.brigadier.Commands.argument("itemName", StringArgumentType.word())
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
                    )
                )
                //
                .then(literal("music")
                        .then(literal("play")
                                .then(io.papermc.paper.command.brigadier.Commands.argument("song", StringArgumentType.word())
                                        .then(io.papermc.paper.command.brigadier.Commands.argument("players", ArgumentTypes.players())
                                                .executes(ctx->{
                                                    String song = ctx.getArgument("song", String.class);
                                                    PlayerSelectorArgumentResolver resolver = ctx.getArgument("players", PlayerSelectorArgumentResolver.class);
                                                    List<Player> players = resolver.resolve(ctx.getSource());
                                                    new MusicMainCommand(ctx, "play", players, song, false);
                                                    return 1;
                                                })
                                                .then(io.papermc.paper.command.brigadier.Commands.argument("loop", BoolArgumentType.bool())
                                                        .executes(ctx -> {
                                                            String song = ctx.getArgument("song", String.class);
                                                            PlayerSelectorArgumentResolver resolver = ctx.getArgument("players", PlayerSelectorArgumentResolver.class);
                                                            List<Player> players = resolver.resolve(ctx.getSource());
                                                            boolean loop = ctx.getArgument("loop", Boolean.class);
                                                            new MusicMainCommand(ctx, "play", players, song, loop);
                                                            return 1;
                                                        })
                                                )
                                        )
                                        .executes(ctx->{
                                            String song = ctx.getArgument("song", String.class);
                                            MusicMainCommand.BuildForSolo(ctx, "play", song, false);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("stop")
                                .then(io.papermc.paper.command.brigadier.Commands.argument("players", ArgumentTypes.players())
                                        .executes(ctx->{
                                            PlayerSelectorArgumentResolver resolver = ctx.getArgument("players", PlayerSelectorArgumentResolver.class);
                                            List<Player> players = resolver.resolve(ctx.getSource());
                                            new MusicMainCommand(ctx, "stop", players, null, false);
                                            return 1;
                                        })
                                )
                                .executes(ctx->{
                                    MusicMainCommand.BuildForSolo(ctx, "stop", null, false);
                                    return 1;
                                })
                        )
                        .then(literal("list")
                                .executes(ctx->{
                                    new MusicMainCommand(ctx);
                                    return 1;
                                })
                        )
                )
                //
                .then(literal("cinematic")
                        .then(literal("record")
                                .then(literal("start")
                                        .then(io.papermc.paper.command.brigadier.Commands.argument("cinematicname", StringArgumentType.word())
                                                .executes(ctx->{
                                                    String cName = ctx.getArgument("cinematicname", String.class);
                                                    new CinematicMainCommand(ctx, "start", cName, 15L);
                                                    return 1;
                                                })
                                                .then(io.papermc.paper.command.brigadier.Commands.argument("period", LongArgumentType.longArg(1L, 100L))
                                                        .executes(ctx -> {
                                                            long delay = ctx.getArgument("period", Long.class);
                                                            String cName = ctx.getArgument("cinematicname", String.class);
                                                            new CinematicMainCommand(ctx, "start", cName, delay);
                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                                .then(literal("stop")
                                        .executes(ctx -> {
                                            new CinematicMainCommand(ctx, "stop", null, 0L);
                                            return 1;
                                        })
                                )
                                .then(literal("pause")
                                        .executes(ctx -> {
                                            new CinematicMainCommand(ctx, "pause", null, 0L);
                                            return 1;
                                        })
                                )
                                .then(literal("resume")
                                        .executes(ctx -> {
                                            new CinematicMainCommand(ctx, "resume", null, 0L);
                                            return 1;
                                        })
                                )
                        )

                        //----
                        .then(literal("play")
                                .then(io.papermc.paper.command.brigadier.Commands.argument("cinematicname", StringArgumentType.word())
                                        .then(io.papermc.paper.command.brigadier.Commands.argument("players", ArgumentTypes.players())
                                                .executes(ctx->{
                                                    String cName = ctx.getArgument("cinematicname", String.class);
                                                    PlayerSelectorArgumentResolver resolver = ctx.getArgument("players", PlayerSelectorArgumentResolver.class);
                                                    List<Player> players = resolver.resolve(ctx.getSource());
                                                    CinematicMainCommand.BuildReproductor(ctx, "play", cName, players, true);
                                                    return 1;
                                                })
                                                .then(io.papermc.paper.command.brigadier.Commands.argument("clone", BoolArgumentType.bool())
                                                        .executes(ctx -> {
                                                            String cName = ctx.getArgument("cinematicname", String.class);
                                                            PlayerSelectorArgumentResolver resolver = ctx.getArgument("players", PlayerSelectorArgumentResolver.class);
                                                            List<Player> players = resolver.resolve(ctx.getSource());
                                                            boolean clone = ctx.getArgument("clone", Boolean.class);
                                                            CinematicMainCommand.BuildReproductor(ctx, "play", cName, players, clone);
                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                        )
                        .then(literal("stop")
                                .then(io.papermc.paper.command.brigadier.Commands.argument("players", ArgumentTypes.players())
                                        .executes(ctx->{
                                            PlayerSelectorArgumentResolver resolver = ctx.getArgument("players", PlayerSelectorArgumentResolver.class);
                                            List<Player> players = resolver.resolve(ctx.getSource());
                                            CinematicMainCommand.BuildReproductor(ctx, "stop", players);
                                            return 1;
                                        })
                                )
                                .executes(ctx->{
                                    CinematicMainCommand.BuildReproductor(ctx, "stop");
                                    return 1;
                                })
                        )
                        .then(literal("list")
                                .executes(ctx->{
                                    CinematicMainCommand.BuildList(ctx.getSource().getSender());
                                    return 1;
                                })
                        )
                )
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
