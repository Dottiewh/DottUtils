package mp.dottiewh.commands.aliasCommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import mp.dottiewh.DottUtils;
import mp.dottiewh.commands.Commands;
import mp.dottiewh.config.CustomConfig;
import mp.dottiewh.music.MusicMainCommand;
import mp.dottiewh.utils.U;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class CommandSequence extends Commands {
    private final String type, name;
    private ConfigurationSection cmdSection = null;
    private final boolean isPlayer;

    public CommandSequence(CommandContext<CommandSourceStack> ctx, @NotNull String type, @NotNull String name) {
        super(ctx);

        this.type=type;
        this.name=name;
        this.isPlayer = sender instanceof Player;

        run();
    }

    @Override
    protected void run(){
        ConfigurationSection mainSection = DottUtils.ymlCommandSequences.getConfig().getConfigurationSection("Sequences");
        if(mainSection==null){
            senderMessage("&cLas secuencias de comandos tienen una estructura inválida! Se recomienda regenerar.");
            return;
        }
        this.cmdSection = mainSection.getConfigurationSection(name);
        if(cmdSection==null){
            senderMessage("&cNo existe la secuencia de comandos &4"+name+"&c en los archivos!");
            return;
        }

        switch(type){
            case "run"-> cmdRun();
            /*case "stop"->{

            }*/
            default->{
                U.mensajeDebugConsole("Command sequence default?");
            }
        }
    }

    private void cmdRun(){
        List<String> commandList = cmdSection.getStringList("commands");
        for(String cmd : commandList){
            if(isPlayer) cmd = checkVariables(cmd);
            U.consoleCommand(cmd);
            U.mensajeDebugConsole("&8> &7"+cmd);
        }
        senderMessage("&aSe ha ejecutado la secuencia de comandos &6"+name+"&a correctamente!");
    }

    private String checkVariables(String cmd){
        if(!cmd.contains("%")) return cmd;
        if(!(sender instanceof Player player)) return cmd;


        if(cmd.contains("%player%")) cmd = cmd.replace("%player%", player.getName());
        if(cmd.contains("%player_x%")) cmd = cmd.replace("%player_x%", String.valueOf(player.getX()));
        if(cmd.contains("%player_y%")) cmd = cmd.replace("%player_y%", String.valueOf(player.getY()));
        if(cmd.contains("%player_z%")) cmd = cmd.replace("%player_z%", String.valueOf(player.getZ()));


        return cmd;
    }

    @NotNull
    public static List<String> getCommandSequences(){
        CustomConfig config = DottUtils.ymlCommandSequences;
        ConfigurationSection commandSections = config.getConfig().getConfigurationSection("Sequences");

        List<String> lista = new ArrayList<>();
        if(commandSections==null){
            U.mensajeConsola("&cLas secuencias de comandos tienen una estructura inválida! Se recomienda regenerar el archivo.");
            return lista;
        }
        lista.addAll(commandSections.getKeys(false));
        return lista;
    }
    //__
    public static LiteralArgumentBuilder<CommandSourceStack> getLiteralBuilder(){
        return literal("cmdSequence")
                .then(literal("run")
                        .then(io.papermc.paper.command.brigadier.Commands.argument("sequence", StringArgumentType.word())
                                .suggests(commandSequence_suggestions)
                                .executes(ctx -> {
                                    String sequence = ctx.getArgument("sequence", String.class);
                                    new CommandSequence(ctx, "run", sequence);
                                    return 1;
                                })
                        )
                );
    }
}
