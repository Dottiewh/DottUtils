package mp.dottiewh.cinematics;

import mp.dottiewh.Commands;
import mp.dottiewh.cinematics.exceptions.CinematicFileDontExist;
import mp.dottiewh.cinematics.exceptions.CinematicRecordingHasNotStarted;
import mp.dottiewh.utils.U;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;

public class CinematicMainCommand extends Commands {
    Player player;

    public CinematicMainCommand(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);

        // /du cinematic record/play/stop
        if (!(sender instanceof Player p)) {
            senderMessageNP("&cEste comando solo lo puede usar un jugador.");
            return;
        }
        this.player = p;

        if (args.length < 2) {
            CinematicsConfig.cineMsg("&cMal uso! Posibles usos &erecord, play, stop", player);
            return;
        }
        run();
    }

    @Override
    protected void run() {
        switch (args[1]) {
            case "record" -> {
                record();
            }
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

    private void record() {
        if(args.length<3){
            CinematicsConfig.cineMsg("&cSubComando no encontrado, &eposibles usos: start, stop, pause, resume&c.", player);
            return;
        }
        switch (args[2]){
            case "start"-> recordStart();
            case "stop"-> recordStop();
            case "pause" -> recordPause();
            case "resume" -> recordResume();
            default-> CinematicsConfig.cineMsg("&cSubComando no encontrado, &eposibles usos: start, stop, pause, resume&c.", player);
        }
    }

    private void recordStart() {
        if(args.length<4){
            senderMessageNP("&cPor favor introduce el nombre de la cinemática");
            return;
        }
        long period = 15L;

        if(!(args.length<5)){
            try{
                period = Long.parseLong(args[4]);
            }catch(Exception e){
                CinematicsConfig.cineMsg("&cIntroduce un número entero de intervalo.", player);
                return;
            }
        }

        CinematicsConfig.startRecording(player, args[3], period);
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
        if(args.length<3){
            senderMessageNP("&cPor favor introduce el nombre de una cinemática");
            return;
        }
        // /du cinematic play test true dott
        if(args.length<4){
            if(CinematicsConfig.reproduceCinematicBoolean(player, args[2], true)){
                CinematicsConfig.cineMsg("&aSe ha reproducido la cinemática &f&l"+args[2]+" &acorrectamente.", player);
            }else{
                CinematicsConfig.cineMsg("&cLa cinemática &f&l"+args[2]+" &cno existe.", player);
            }
            return;
        }
        boolean clone = (Boolean.parseBoolean(args[3]));

        if(args.length<5){
            if(CinematicsConfig.reproduceCinematicBoolean(player, args[2], clone)){
                CinematicsConfig.cineMsg("&aSe ha reproducido la cinemática &f&l"+args[2]+" &acorrectamente.", player);
            }else{
                CinematicsConfig.cineMsg("&cLa cinemática &f&l"+args[2]+" &cno existe.", player);
            }
            return;
        }

        Player target = Bukkit.getPlayerExact(args[4]);
        if(args[4].equalsIgnoreCase("all")){
            try{
                CinematicsConfig.reproduceCinematicForAll(args[2], clone);
                CinematicsConfig.cineMsg("&aSe ha reproducido la cinemática &f&l"+args[2]+" &acorrectamente a todos.", player);
                return;
            } catch (CinematicFileDontExist e) {
                CinematicsConfig.cineMsg("&cLa cinemática &f&l"+args[2]+" &cno existe.", player);
                return;
            }
        }
        if(target==null){
            CinematicsConfig.cineMsg("&cEl jugador &f"+args[2]+" &cno está conectado.", player);
            return;
        }

        try{
            CinematicsConfig.reproduceCinematic(target, args[2], clone, true);
            CinematicsConfig.cineMsg("&aSe ha reproducido la cinemática &f&l"+args[2]+" &acorrectamente a &f"+target.getName()+"&a.", player);
            return;
        } catch (CinematicFileDontExist e) {
            CinematicsConfig.cineMsg("&cLa cinemática &f&l"+args[2]+" &cno existe.", player);
            return;
        }
    }

    // /du cinematic[0] stop[1] player[2]
    private void stop(){
        if(args.length<3){
            CinematicsConfig.stopReproducing(player.getUniqueId());
            CinematicsConfig.cineMsg("&aSe ha mandado la orden de parar cualquier cinemática.", player);
            return;
        }
        Player target = Bukkit.getPlayerExact(args[2]);
        if(args[2].equalsIgnoreCase("all")){
            CinematicsConfig.stopReproducingForAll();
            CinematicsConfig.cineMsg("&aSe ha mandado la orden de pararle cualquier cinemática a todos.", player);
            return;
        }
        if(target==null){
            CinematicsConfig.cineMsg("&cEl jugador &f"+args[2]+" &cno está conectado.", player);
            return;
        }
        CinematicsConfig.stopReproducing(target.getUniqueId());
        CinematicsConfig.cineMsg("&aSe ha mandado la orden de pararle cualquier cinemática a &6"+args[2]+"&a.", player);
    }


}
