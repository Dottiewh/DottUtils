package mp.dottiewh.noaliasCommands;

import mp.dottiewh.Commands;
import mp.dottiewh.Utils.U;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Jump extends Commands {
    private static final Map<UUID, BukkitTask> delayMap = new HashMap<>();
     // in ticks

    public Jump(Set<String> comandosRegistrados, CommandSender sender, Command command, String label, String[] args) {
        super(comandosRegistrados, sender, command, label, args);

        run();
    }

    @Override
    protected void run() {

        long delayNoFall = 600L;
        String errorMsg = "&c&lNo has introducido un valor valido.\n&6Posibles valores: &eNúmero | &e&oNombre de un jugador &8| &e&oNúmero";

        int times = 1;
        double input;
        boolean toOther = false;

        if (!(sender instanceof Player)) { //determina si es una consola sin especificar un jugador
            if (args.length < 2) {
                senderMessage("&c&lEste comando solo lo puede usar un jugador. &e&o(A menos que especifiques a uno)");
                return;
            }
        }
        if (args.length == 0) { //determina si no se especificó un impulso
            senderMessage(errorMsg);
            return;
        }
        try { //Determina si no se introdució un impulso apto (no double)
            input = Double.parseDouble(args[0]);
        } catch (Exception e) {
            senderMessage(errorMsg);
            return;
        }
        Player player;
        //checks of bugs
        if (sender instanceof Player) player = (Player) sender;
        else player = Bukkit.getPlayer(args[1]);

        // searching for times
        if (args.length > 1) {
            try {
                times = Integer.parseInt(args[1]);
            } catch (Exception e) {
                if (args.length > 2) {
                    try {
                        times = Integer.parseInt(args[2]);
                    } catch (Exception e2) {
                        System.out.println("well");
                    }
                }
            }
        }
        //just checking if the command was for another player
        if (args.length > 1) {
            player = Bukkit.getPlayer(args[1]);
            toOther = true;

        }
        if (player == null) {
            if (!(sender instanceof Player && times != 1)) {
                senderMessage("&c&lHas introducido un jugador no conectado.");
                return;
            }
            else {toOther = false; player = (Player) sender;}
        }
        if (times > 50) {senderMessage("&cBro controlate &e(max 50)&c."); return;}
        Player target = player;

        if (delayMap.containsKey(target.getUniqueId())) delayMap.get(target.getUniqueId()).cancel(); // se cancela si hay una tarea
        //------end of checks


        for (int i = 0; i < times; i++) {
            int delay = 5 * i;

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                target.setVelocity(new Vector(0, input, 0));
            }, delay);

        }


        // anti caída
        U.noFall_add(target);

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                delayMap.remove(target.getUniqueId());
                U.noFall_remove(target);
            }
        }.runTaskLater(plugin, delayNoFall); // delay = espera

        // Guardamos la nueva tarea
        delayMap.put(target.getUniqueId(), task);

        if (toOther){
            senderMessage("&aHas hecho saltar a: &f"+player.getName()+" &8| &e(Input: "+args[0]+") &7&i("+times+")");
        }


    }
}
