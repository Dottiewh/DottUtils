package mp.dottiewh.music;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.Commands;
import mp.dottiewh.utils.U;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MusicMainCommand extends Commands {
    private static String musicPrefix = "&d&l[&9&lMusica&d&l] ";
    String type, songName;
    boolean loop;

    Player player;
    public static void buildCommand(CommandContext<CommandSourceStack> ctx, String type, List<Player> pCollection, String song, boolean loop) {
        for(Player p : pCollection){
            new MusicMainCommand(ctx, type, p, song, loop);
        }
    }
    private MusicMainCommand(CommandContext<CommandSourceStack> ctx, String type, Player p, String song, boolean loop) {
        super(ctx);
        this.player=p;
        this.type=type;
        this.songName=song;
        this.loop=loop;
        run();
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
        try{
            deliverSongCore(songName, player, loop);
            senderMessageMPr("&aHas reproducido la canción &f"+songName+"&a a "+player.getName()+".");
        } catch (Exception e) {
            senderMessageMPr("&cHa ocurrido un error intentando reproducir "+songName+" (Posiblemente no exista la canción)");

            U.mensajeConsolaNP("&c"+ Arrays.toString(e.getStackTrace()));
        }
    }
    //
    private void deliverSongCore(String song, Player player, boolean loop){
        MusicConfig.reproduceTo(song, player, loop);
    }

    private void stop(){
        MusicConfig.stopMusicTasks(player.getUniqueId());
        senderMessageMPr("Has parado todas las reproducciones a "+player.getName());
    }

    //---
    public static void setMusicPrefix(String prefix){
        musicPrefix=prefix;
    }

    private void senderMessageMPr(String msg){
        senderMessageNP(musicPrefix+msg);
    }
}
