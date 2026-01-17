package mp.dottiewh.cinematics;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.Commands;
import mp.dottiewh.ReferibleCommand;
import mp.dottiewh.cinematics.exceptions.CinematicFileDontExist;
import mp.dottiewh.cinematics.exceptions.CinematicInternalError;
import mp.dottiewh.cinematics.exceptions.CinematicRecordingHasNotStarted;
import mp.dottiewh.utils.U;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
        if(cinematicName==null) throw new CinematicInternalError("Nombre de la cinematica es null?","recordStart in CinematicMainCommand");

        long period = delay;
        if(period<0) period=15L;

        CinematicsConfig.startRecording(player, cinematicName, period);
    }
    private void recordStop(){
        CinematicsConfig.stopRegister(player);
    }
    private void recordPause(){
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
            CinematicsConfig.cineMsg("&aSe ha reproducido la cinemática &f&l"+cinematicName+" &acorrectamente a &f"+output+"&a.", sender);

        }else{
            CinematicsConfig.cineMsg("&cLa cinemática &f&l"+cinematicName+" &cno existe.", sender);
        }


    }

    // /du cinematic[0] stop[1] player[2]
    private void stop(){
        for(Player t : playerList){
            CinematicsConfig.stopReproducing(t.getUniqueId());
        }
        String output = String.join("&8, &6", getPlayerNameList());
        CinematicsConfig.cineMsg("&aSe ha mandado la orden de pararle cualquier cinemática a &6"+output+"&a.", sender);


    }
    private static void list(CommandSender sender){
        List<String> aList =CinematicsConfig.getCinematicsName();
        String output = String.join("&8, &f", aList);
        CinematicsConfig.cineMsg("&6&lLas cinemáticas guardadas son: &f"+output, sender);
    }

}
