package mp.dottiewh.music;

import mp.dottiewh.Commands;
import mp.dottiewh.utils.U;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;

public class MusicMainCommand extends Commands {
    private static String musicPrefix = "&d&l[&9&lMusica&d&l] ";
    Player player;

    public MusicMainCommand(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);

        if (!(sender instanceof Player)){
            senderMessageMPr("&cEste comando solo lo puede usar un jugador.");
            return;
        }
        
        this.player= (Player) sender;
        run();
    }

    @Override
    protected void run() {
        String errorMsg = "&cNo has usado bien el comando.\n&6Posibles usos: &eplay, stop";
        //Check
        if (args.length<2){
            senderMessageMPr(errorMsg);
            return;
        }

        switch (args[1]){
            case "play" -> play();
            case "stop" -> stop();

            default -> senderMessageMPr(errorMsg);
        }
    }
    private void play(){
        switch(args.length){
            // sin cancion
            case 2->{
                senderMessageMPr("&cPor favor introduce una canción.");
            }
            // con cancion
            case 3->{
                try{
                    MusicConfig.reproduceTo(args[2], player, false);
                    senderMessageMPr("&aSe te ha reproducido a ti la canción de: &e"+args[2]);
                } catch (Exception e) {
                    senderMessageMPr("&cHa ocurrido un error. (check console)");
                    U.mensajeConsolaNP("&c"+ Arrays.toString(e.getStackTrace()));
                }
            }
            //con canción y nombre
            case 4->{
                boolean success = false;

                if(args[3].equals("all")){
                    for(Player online : Bukkit.getOnlinePlayers()){
                        try{
                            deliverSongCore(args[2], online, false);
                        } catch (Exception e) {
                            senderMessageMPr("&cHa ocurrido un error al intentar reproducirle a" +online.getName()+" (check console)");
                            senderMessageMPr("&cCanción: &4"+args[2]);
                            U.mensajeConsolaNP("&c"+ Arrays.toString(e.getStackTrace()));
                            break;
                        }
                    }
                    senderMessageMPr("&aHas reproducido la canción &f"+args[2]+"&a a todos los jugadores conectados.");
                    return;
                }
                Player toDeliver = Bukkit.getPlayer(args[3]);
                if(toDeliver==null){
                    senderMessageMPr("&cEl jugador "+args[3]+" no está conectado.");
                    return;
                }
                try{
                    deliverSongCore(args[2], toDeliver, false);
                    senderMessageMPr("&aHas reproducido la canción &f"+args[2]+"&a a "+args[3]+".");
                } catch (Exception e) {
                    senderMessageMPr("&cHa ocurrido un error intentando reproducir "+args[2]+" (check console)");
                    U.mensajeConsolaNP("&c"+ Arrays.toString(e.getStackTrace()));
                }
            }
            //con canción nombre y si es loop
            default->{
                boolean loop = Boolean.parseBoolean(args[4]);

                if(args[3].equals("all")){
                    for(Player online : Bukkit.getOnlinePlayers()){
                        try{
                            deliverSongCore(args[2], online, loop);
                        } catch (Exception e) {
                            senderMessageMPr("&cHa ocurrido un error al intentar reproducirle a" +online.getName()+" (check console)");
                            senderMessageMPr("&cCanción: &4"+args[2]);
                            U.mensajeConsolaNP("&c"+ Arrays.toString(e.getStackTrace()));
                            break;
                        }
                    }
                    senderMessageMPr("&aHas reproducido la canción &f"+args[2]+"&a a todos los jugadores conectados. &7(loop: "+loop+")");
                    return;
                }
                Player toDeliver = Bukkit.getPlayerExact(args[3]);
                if(toDeliver==null){
                    senderMessageMPr("&cEl jugador "+args[3]+" no está conectado.");
                    return;
                }
                try{
                    deliverSongCore(args[2], toDeliver, loop);
                    senderMessageMPr("&aHas reproducido la canción &f"+args[2]+"&a a "+args[3]+". &7(loop: "+loop+")");
                } catch (Exception e) {
                    senderMessageMPr("&cHa ocurrido un error intentando reproducir "+args[2]+" (check console)");
                    U.mensajeConsolaNP("&c"+ Arrays.toString(e.getStackTrace()));
                }
            }
        }
    }
    //
    private void deliverSongCore(String song, Player player, boolean loop){
        MusicConfig.reproduceTo(song, player, loop);
    }

    private void stop(){
        if(args.length==2){
            MusicConfig.stopMusicTasks(player.getUniqueId());
            senderMessageMPr("&aSe te han finalizado todas las canciones en reproducción.");
            return;
        }
        if(args[2].equalsIgnoreCase("all")){
            MusicConfig.stopMusicTasks();
            senderMessageMPr("&aHas finalizado la reproducción de canciones a todos los jugadores.");
            return;
        }
        //
        Player toDeliver = Bukkit.getPlayer(args[2]);
        if(toDeliver==null){
            senderMessageMPr("&cEl jugador &4"+args[2]+" &cno está conectado.");
            return;
        }

        MusicConfig.stopMusicTasks(toDeliver.getUniqueId());
        senderMessageMPr("&aLe has finalizado la reproducción de cualquier canción a &f"+args[2]+"&a correctamente.");
    }

    //---
    public static void setMusicPrefix(String prefix){
        musicPrefix=prefix;
    }

    private void senderMessageMPr(String msg){
        senderMessageNP(musicPrefix+msg);
    }
}
