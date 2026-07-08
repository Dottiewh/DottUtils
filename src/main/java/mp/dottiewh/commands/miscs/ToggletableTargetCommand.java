package mp.dottiewh.commands.miscs;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mp.dottiewh.commands.Commands;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public abstract class ToggletableTargetCommand extends Commands implements ToggletableFunctions{
    protected Player player;
    protected UUID playerUUID;
    private String cmdId;

    private static final HashMap<String, List<UUID>> uuidBooleanMap = new HashMap<>();

    public ToggletableTargetCommand(CommandContext<CommandSourceStack> ctx, String cmdId) {
        super(ctx);

        if(!(sender instanceof Player p)){
            senderMessageNP("&cEste comando solo lo puede usar un jugador!");
            return;
        }
        this.player=p;
        this.playerUUID = player.getUniqueId();
        this.cmdId = cmdId;

        uuidBooleanMap.putIfAbsent(cmdId, new LinkedList<>()); //
        //getStatusMainMap().putIfAbsent(playerUUID, false);

        run();
    }

    @Override
    protected void run() {
        doBeforeRun();

        boolean status = getUuidList().contains(playerUUID);

        if(status){
            onDisable();
            getUuidList().remove(playerUUID);
        }
        else{
            onEnable();
            getUuidList().add(playerUUID);
        }

    }
    protected void doBeforeRun(){

    }

    //---
    protected List<UUID> getUuidList(){
        return uuidBooleanMap.get(cmdId);
    }
}
