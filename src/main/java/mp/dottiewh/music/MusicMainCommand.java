package mp.dottiewh.music;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.Commands;
import mp.dottiewh.ReferibleCommand;
import mp.dottiewh.items.Exceptions.ItemSectionEmpty;
import mp.dottiewh.items.ItemConfig;
import mp.dottiewh.utils.U;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class MusicMainCommand extends ReferibleCommand {
    private static String musicPrefix = "&d&l[&9&lMusica&d&l] ";
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
        if(!isListEmpty) return;

        switch (type){
            case "play" -> play();
            case "stop" -> stop();

            default -> senderMessageMPr(errorMsg);
        }
    }
    private void play(){
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
            senderMessageMPr("&aHas reproducido la canción &f"+songName+"&a a &f"+getOutput("&f")+"&a.");
        }
    }
    //
    private void deliverSongCore(String song, Player player, boolean loop){
        MusicConfig.reproduceTo(song, player, loop);
    }

    private void stop(){
        playerList.forEach(p->MusicConfig.stopMusicTasks(p.getUniqueId()));
        senderMessageMPr("Has parado todas las reproducciones a &f"+getOutput("&f"));
    }
    private void list(){
        Set<String> musicas;
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

    private void senderMessageMPr(String msg){
        senderMessageNP(musicPrefix+msg);
    }
}
