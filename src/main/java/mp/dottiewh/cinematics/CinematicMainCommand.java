package mp.dottiewh.cinematics;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import mp.dottiewh.commands.ReferibleCommand;
import mp.dottiewh.cinematics.exceptions.CinematicFileDontExist;
import mp.dottiewh.cinematics.exceptions.CinematicInternalError;
import mp.dottiewh.cinematics.exceptions.CinematicRecordingHasNotStarted;
import mp.dottiewh.commands.aliasCommands.Reload;
import mp.dottiewh.utils.U;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class CinematicMainCommand extends ReferibleCommand {
    Player player;
    String type;
    String cinematicName;
    boolean clone=false;

    public static void BuildList(CommandSender sender){
        list(sender);
    }
    public static void BuildReproductor(CommandContext<CommandSourceStack> ctx, String type){
        CommandSender s = ctx.getSource().getSender();
        if(!(s instanceof Player p)){
            CinematicsConfig.cineMsg("&cEl comando usado de esta manera solo lo puede usar un jugador!", s);
            CinematicsConfig.cineMsg("&6Prueba a referirte a un jugador o @a!", s);
            return;
        }
        List<Player> tempList = new ArrayList<>();
        tempList.add(p);
        new CinematicMainCommand(ctx, type, null, tempList, false);
    }
    public static void BuildReproductor(CommandContext<CommandSourceStack> ctx, String type, List<Player> playerList){
        new CinematicMainCommand(ctx, type, null, playerList, false);
    }
    public static void BuildReproductor(CommandContext<CommandSourceStack> ctx, String type, String cinematicName, List<Player> playerList, boolean clone){
        new CinematicMainCommand(ctx, type, cinematicName, playerList, clone);
    }

    @ApiStatus.Internal
    private CinematicMainCommand(CommandContext<CommandSourceStack> ctx, String fileName){
        super(ctx);
        delete(fileName);
    }
    @ApiStatus.Internal
    private CinematicMainCommand(CommandContext<CommandSourceStack> ctx, CommandSender sender, String type, String cinematicName, boolean clone) {
        super(ctx, sender);
        if(isListEmpty) return;

        this.type = type;
        this.cinematicName = cinematicName;
        this.clone = clone;

        run();
    }

    private CinematicMainCommand(CommandContext<CommandSourceStack> ctx, String type, String cinematicName, List<Player> playerList, boolean clone) {
        super(ctx, playerList);
        if(isListEmpty) return;

        this.type=type;
        this.cinematicName=cinematicName;
        this.clone=clone;
        run();
    }
    //
    public CinematicMainCommand(CommandContext<CommandSourceStack> ctx, String recordType, String cinematicName, long delay) {
        super(ctx);
        if(!(sender instanceof Player p)){
            CinematicsConfig.cineMsg("&cEste tipo de comando solo lo puede usar un jugador.", sender);
            return;
        }
        this.player=p;
        this.cinematicName=cinematicName;
        record(recordType, delay);
    }

    @Override
    protected void run() {
        if(playerList.isEmpty()){
            CinematicsConfig.cineMsg("&e&lNo te has referido a ningun jugador!", sender);
            return;
        }
        switch (type) {
            case "play" -> {
                play();
            }
            case "stop" -> {
                stop();
            }
            default -> {
                CinematicsConfig.cineMsg("&cComando no encontrado.", player);
            }
        }
    }

    private void record(String recType, long delay) {
        switch (recType){
            case "start"-> recordStart(delay);
            case "stop"-> recordStop();
            case "pause" -> recordPause();
            case "resume" -> recordResume();
            default-> CinematicsConfig.cineMsg("&cSubComando no encontrado, &eposibles usos: start, stop, pause, resume&c.", player);
        }
    }

    private void recordStart(long delay) {
        debugMsg("CinematicMainCommand.recordStart");
        if(cinematicName==null) throw new CinematicInternalError("Nombre de la cinematica es null?","recordStart in CinematicMainCommand");

        long period = delay;
        if(period<0) period=15L;

        CinematicsConfig.startRecording(player, cinematicName, period);
    }
    private void recordStop(){
        debugMsg("CinematicMainCommand.recordStop");
        CinematicsConfig.stopRegister(player);
    }
    private void recordPause(){
        debugMsg("CinematicMainCommand.recordPause");
        try{
            CinematicsConfig.pauseRecord(player);
            CinematicsConfig.cineMsg("&eHas pausado tu grabación, usa &6/du cinematic record resume &epara resumirla.", player);
            U.playsoundTarget(player, Sound.BLOCK_BEACON_DEACTIVATE, 10f, 1f);
        }catch(CinematicRecordingHasNotStarted e){
            CinematicsConfig.cineMsg("&cNo estás grabando ninguna cinemática", player);
            U.mensajeConsolaNP("&e"+ Arrays.toString(e.getStackTrace()));
        }
    }
    private void recordResume(){
        debugMsg("CinematicMainCommand.recordResume");
        try{
            CinematicsConfig.resumeRecord(player);
            U.playsoundTarget(player, Sound.BLOCK_BEACON_ACTIVATE, 10f, 1f);
        }catch(CinematicRecordingHasNotStarted e){
            CinematicsConfig.cineMsg("&cNo estás grabando ninguna cinemática", player);
            U.mensajeConsolaNP("&e"+ Arrays.toString(e.getStackTrace()));
        }
    }

    //--
    private void play(){
        debugMsg("CinematicMainCommand.play");
        if(cinematicName==null) throw new CinematicInternalError("Nombre de la cinematica es null al intentar reproducir cinematica?","play in CinematicMainCommand");

        // /du cinematic play test
        boolean success=true;
        for(Player t : playerList){
            try{
                CinematicsConfig.reproduceCinematic(t, cinematicName, clone, true);
            } catch (CinematicFileDontExist e) {
                success=false;
                break;
            }
        }
        if(success){
            String output = String.join("&8, &f", getPlayerNameList());
            CinematicsConfig.cineMsg("&aSe ha reproducido la cinemática &f&l"+cinematicName+" &acorrectamente a &f"+output+"&a. &e("+clone+")", sender);

        }else{
            CinematicsConfig.cineMsg("&cLa cinemática &f&l"+cinematicName+" &cno existe.", sender);
        }


    }

    // /du cinematic[0] stop[1] player[2]
    private void stop(){
        debugMsg("CinematicMainCommand.stop");
        for(Player t : playerList){
            CinematicsConfig.stopReproducing(t.getUniqueId());
        }
        String output = String.join("&8, &6", getPlayerNameList());
        CinematicsConfig.cineMsg("&aSe ha mandado la orden de pararle cualquier cinemática a &6"+output+"&a.", sender);


    }
    private static void list(CommandSender sender){
        U.mensajeDebug("CinematicMainCommand.list", sender);
        List<String> aList =CinematicsConfig.getCinematicsName();
        if(aList==null){
            CinematicsConfig.cineMsg("&cNo tienes cinemáticas guardadas aún!", sender);
            return;
        }
        String output = String.join("&8, &f", aList);
        CinematicsConfig.cineMsg("&6&lLas cinemáticas guardadas son: &f"+output, sender);
    }
    private void delete(String fileName){
        debugMsg("CinematicMainCommand.delete");
        try{
            if(CinematicsConfig.deleteCinematic(fileName)){
                CinematicsConfig.cineMsg("&aHas borrado la cinemática &e"+fileName+" &acorrectamente!", sender);
            }else{
                CinematicsConfig.cineMsg("&4&lAlgo pasó.", sender);
            }

        }catch (CinematicFileDontExist e){
            CinematicsConfig.cineMsg("&c&lLa cinemática '"+fileName+"' probablemente no existe.", sender);
            return;
        }
    }

    //----------------------------------
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("cinematic")
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
                                .executes(ctx -> {
                                    String cName = ctx.getArgument("cinematicname", String.class);
                                    new CinematicMainCommand(ctx, ctx.getSource().getSender(), "play", cName, true);
                                    return 1;
                                })
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
                .then(literal("delete")
                        .then(io.papermc.paper.command.brigadier.Commands.argument("cinematicName", StringArgumentType.word())
                                .executes(ctx->{
                                    String fileName = ctx.getArgument("cinematicName", String.class);
                                    new CinematicMainCommand(ctx, fileName);
                                    return 1;
                                })
                        )
                )
                ;
    }
}
