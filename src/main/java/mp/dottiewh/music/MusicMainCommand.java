package mp.dottiewh.music;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import mp.dottiewh.commands.ReferibleCommand;
import mp.dottiewh.commands.aliasCommands.Reload;
import mp.dottiewh.items.Exceptions.ItemSectionEmpty;
import mp.dottiewh.items.ItemMainCommand;
import mp.dottiewh.utils.U;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class MusicMainCommand extends ReferibleCommand {
    private static final Logger log = LoggerFactory.getLogger(MusicMainCommand.class);
    protected static String musicPrefix = "&d&l[&9&lMusica&d&l] ";
    String type, songName;
    boolean loop;


    public static void BuildForSolo(CommandContext<CommandSourceStack> ctx, String type, String song, boolean loop){
        CommandSender sender = ctx.getSource().getSender();
        if(!(sender instanceof Player p)){
            U.targetMessageNP(sender, musicPrefix+"&cEsta referenciación solo la puede usar un jugador!");
            U.targetMessageNP(sender, musicPrefix+"&6Prueba a usar un @a o jugador!");
            return;
        }
        List<Player> pList  = new ArrayList<>();
        pList.add(p);
        new MusicMainCommand(ctx, type, pList, song, loop);
    }

    // DO NOT PUT NOTHING ON IT
    protected MusicMainCommand(){
    }

    public MusicMainCommand(CommandContext<CommandSourceStack> ctx, String type, List<Player> pList, String song, boolean loop) {
        super(ctx, pList);

        this.type = type;
        this.songName = song;
        this.loop = loop;

        if(isListEmpty) return;
        run();
    }

    @ApiStatus.Internal
    public MusicMainCommand(CommandContext<CommandSourceStack> ctx) {
        super(ctx);
        list();
    }


    @Override
    protected void run() {
        String errorMsg = "&cNo has usado bien el comando.\n&6Posibles usos: &eplay, stop";

        switch (type){
            case "play" -> play();
            case "stop" -> stop();

            default -> senderMessageMPr(errorMsg);
        }
    }
    private void play(){
        debugMsg("ItemMainCommand.play");
        boolean success=true;
        for(Player p : playerList){
            try{
                deliverSongCore(songName, p, loop);
            } catch (Exception e) {
                senderMessageMPr("&cHa ocurrido un error intentando reproducir "+songName+" (Posiblemente no exista la canción)");
                U.mensajeConsolaNP("&c"+e);
                success=false;
                break;
            }
        }
        if(success){
            senderMessageMPr("&aHas reproducido la canción &f"+songName+"&a a &f"+getOutput("&f")+"&a. &e("+loop+")");
        }
    }
    //
    private void deliverSongCore(String song, Player player, boolean loop){
        MusicConfig.reproduceTo(song, player, loop);
    }

    private void stop(){
        debugMsg("ItemMainCommand.stop");
        playerList.forEach(p->MusicConfig.stopMusicTasks(p.getUniqueId()));
        senderMessageMPr("Has parado todas las reproducciones a &f"+getOutput("&f"));
    }
    private void list(){
        debugMsg("ItemMainCommand.list");

        List<String> musicas;
        try{
            musicas = MusicConfig.getMusicList();
        }catch (ItemSectionEmpty e){
            senderMessageMPr("Hay un error en tu yml, consola para detalles.");
            U.mensajeConsola(e.toString());
            return;
        }

        String musicList = String.join("&8, &f", musicas);
        senderMessageMPr("&aLista de musicas registradas: &f"+musicList);
    }

    //---
    public static void setMusicPrefix(String prefix){
        musicPrefix=prefix;
    }

    protected void senderMessageMPr(String msg){
        senderMessageNP(musicPrefix+msg);
    }
    protected static void senderMessageMPr(CommandSender sender, String msg){
        U.targetMessageNP(sender, musicPrefix+msg);
    }

    //----------------------------------
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("music")
                .requires(ctx->ctx.getSender().hasPermission("DottUtils.music"))
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
                .then(literal("menu")
                        .executes(ctx->{
                            MusicFront.buildFront(ctx);
                            return 1;
                        })
                )
                ;
    }
}
