package mp.dottiewh.cinematics;

import mp.dottiewh.Commands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            senderMessageNP("&cMal uso, por favor introduce si se trata de &estart, stop");
            return;
        }
        switch (args[2]){
            case "start"-> recordStart();
            case "stop"-> recordStop();
            default-> CinematicsConfig.cineMsg("&cSubComando no encontrado.", player);
        }
    }

    private void recordStart() {
        if(args.length<4){
            senderMessageNP("&cPor favor introduce el nombre de la cinemática");
            return;
        }
        long period = 1L;

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
    //--
    private void play(){
        if(args.length<3){
            senderMessageNP("&cPor favor introduce el nombre de una cinemática");
            return;
        }
        // /du cinematic play test
        if(args.length<4){
            if(CinematicsConfig.reproduceCinematic(player, args[2], true)){
                CinematicsConfig.cineMsg("&aSe ha reproducido la cinemática &f&l"+args[2]+" &acorrectamente.", player);
            }else{
                CinematicsConfig.cineMsg("&cLa cinemática &f&l"+args[2]+" &cno existe.", player);
            }
            return;
        }
        boolean clone = (Boolean.parseBoolean(args[3]));

        if(CinematicsConfig.reproduceCinematic(player, args[2], clone)){
            CinematicsConfig.cineMsg("&aSe ha reproducido la cinemática &f&l"+args[2]+" &acorrectamente.", player);
        }else{
            CinematicsConfig.cineMsg("&cLa cinemática &f&l"+args[2]+" &cno existe.", player);
        }

    }
    private void stop(){

    }

}
