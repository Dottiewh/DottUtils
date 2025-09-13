package mp.dottiewh.aliasCommands;

import io.papermc.paper.event.player.AsyncChatEvent;
import mp.dottiewh.Commands;
import mp.dottiewh.utils.U;
import mp.dottiewh.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AdminChat extends Commands {
    private static final Map<String, Boolean> adminchatStatus = new HashMap<>();
    private static final Map<String, Boolean> acIsJoined = new HashMap<>();
    private static String acPrefix = U.getMsgPath("adminchat_prefix"); //"&6&l[&e&lAdmin&9&lChat&6&l] &7";
    private static final String errorMsg = "&cHas usado un término incorrecto.\n&6Posibles usos: &etoggle, leave, join";
    String dName;
    private static final String[] comandosProtegidos = {"du ac toggle", "du adminchat toggle", "du achat toggle",
        "ac toggle", "achat toggle", "adminchat toggle"};

    //normal case (/du ac)
    public AdminChat(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args, Boolean isNoOpCase) {
        super(comandosRegistrados, sender, command, label, args);

        if (isNoOpCase){
            runNoOp();
        }else{
            run();
        }
    }

    @Override
    protected void run() {
        if (somethingFailedOnCheck()) return;

        if (args.length<2){
            senderMessage(errorMsg);
            return;
        }
        switch (args[1]){
            case "toggle"-> toggle();
            case "leave" -> leave();
            case "join" -> join();

            default -> senderMessage(errorMsg);
        }

    }
    protected void runNoOp() {
        if (somethingFailedOnCheck()) return;


        if (args.length<1){
            senderMessage(errorMsg);
            return;
        }
        switch (args[0]){
            case "toggle"-> toggle();
            case "leave" -> leave();
            case "join" -> join();

            default -> senderMessage(errorMsg);
        }

    }

    private boolean somethingFailedOnCheck(){
        if (!(sender instanceof Player || sender instanceof ConsoleCommandSender)) return true;

        if (sender instanceof Player player){
            if (!Config.containsAdmin(player.getName())){
                senderMessage("&c&lNo estás registrado como admin.");
                return true;
            }
            this.dName = player.getName();
        }
        if (sender instanceof ConsoleCommandSender console){
            if (!Config.containsAdmin("Console") || !Config.containsAdmin("console")){
                senderMessageNP(acPrefix+"&c&lNo estás registrado como admin.");
                return true;
            }
            this.dName = "Console";

        }
        return false;
    }
    //-----------
    private void toggle(){
        adminchatStatus.putIfAbsent(dName, false);

        if (adminchatStatus.get(dName)) {//cambia el estado de true a false
            adminchatStatus.replace(dName, true, false);
            senderMessage("&9Mensajes de admin al hablar &cDesactivado&9!");
        }
        else{
            adminchatStatus.replace(dName, false, true);
            senderMessage("&9Mensajes de admin al hablar &aActivado&9!");
            senderMessage("&aHabla de manera normal por el chat!");
        }
    }
    private void leave(){
        acIsJoined.putIfAbsent(dName, true);
        if (Boolean.FALSE.equals(acIsJoined.get(dName))){
            senderMessage("&cNo estabas en el canal de AdminChat.");
            return;
        }

        acIsJoined.replace(dName, true, false);
        senderMessage("&4Te has salido del canal de Admins!");
    }
    private void join(){
        acIsJoined.putIfAbsent(dName, true);
        if (Boolean.TRUE.equals(acIsJoined.get(dName))){
            senderMessage("&cYa estás en el canal de AdminChat.");
            return;
        }

        acIsJoined.replace(dName, false, true);
        senderMessage("&eTe has unido del canal de Admins!");
    }

    public static void acCore(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();

        if (!Config.containsAdmin(name)) return; // se devuelve si X no es admin
        //UUID uuid = player.getUniqueId();
        if (!adminchatStatus.containsKey(name)) return; //se devuelve si no hay datos de tal admin
        if (!adminchatStatus.get(name)) return; //Se devuelve si el tipo tiene off el ac



        String msg = U.componentToStringMsg(event.originalMessage());
        event.setCancelled(true);

        for (String adm : Config.getAdminList()) {
            Player target = Bukkit.getPlayer(adm);
            if (target != null && target.isOnline()) {
                if (Boolean.FALSE.equals(acIsJoined.get(target.getName()))) continue;// null o true pasa
                U.targetMessageNP(target, acPrefix+name+" &8&l> &f"+msg);
            }
        }
        consoleCore(name, msg);

        if (Boolean.FALSE.equals(acIsJoined.get(name))){
            player.sendMessage(U.mensajeConColor(acPrefix+"&6&lEstás escribiendo en el canal de admin, sin estar dentro!"));
            player.sendMessage(U.mensajeConColor(acPrefix+"&e&lIntenta: &e/du ac join &8|&e /du ac toggle"));
        }
    }
    private static void consoleCore(String by, String msg){

        if (Boolean.FALSE.equals(acIsJoined.get("Console"))) return;

        if (Config.containsAdmin("Console")){
               U.mensajeConsolaNP(acPrefix+by+" &8&l> &f"+msg);
        }
    }

    public static void consoleChatCore(ServerCommandEvent event){
        if (!(event.getSender() instanceof ConsoleCommandSender console)) return;
        if (!Boolean.TRUE.equals(adminchatStatus.get("Console"))) return;
        String input = event.getCommand();

        if (isProtectedCommand(input)) return;

        event.setCancelled(true);

        console.sendMessage(U.mensajeConColor("&eTienes el modo Admin chat activado! &6Puedes usar /du ac toggle."));
        for (String adm : Config.getAdminList()) {
            Player target = Bukkit.getPlayer(adm);
            if (target != null && target.isOnline()) {
                if (Boolean.FALSE.equals(acIsJoined.get(target.getName()))) continue;// null o true pasa
                U.targetMessageNP(target, acPrefix+"Console"+" &8&l> &f"+input);
            }
        }
        consoleCore("Console", input);

    }
    private static boolean isProtectedCommand(String cmd){
        return Arrays.asList(comandosProtegidos).contains(cmd.toLowerCase());
    }

    public static void acPrefixReload(){
        acPrefix = U.getMsgPath("adminchat_prefix");
    }
}
