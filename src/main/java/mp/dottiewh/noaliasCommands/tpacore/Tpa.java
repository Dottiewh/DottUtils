package mp.dottiewh.noaliasCommands;

import mp.dottiewh.Commands;
import mp.dottiewh.utils.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class Tpa extends Commands {
    private static String prefix;

    public Tpa(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);

        prefix = U.getMsgPath("tpa_prefix", "&6&l[&e&lTpa&6&l] &f");
        run();
    }

    @Override
    protected void run() {
        if (!(sender instanceof Player player)){

        }
    }

    private void senderMsgPr(String msg){
        senderMessageNP(prefix+msg);
    }
}
