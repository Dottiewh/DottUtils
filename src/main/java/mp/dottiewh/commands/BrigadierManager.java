package mp.dottiewh.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import mp.dottiewh.DottUtils;
import mp.dottiewh.cinematics.CinematicMainCommand;
import mp.dottiewh.cinematics.CinematicsConfig;
import mp.dottiewh.commands.aliasCommands.*;
import mp.dottiewh.commands.noaliasCommands.*;
import mp.dottiewh.commands.noaliasCommands.backcore.BackCommand;
import mp.dottiewh.commands.noaliasCommands.playtimecore.PlayTime;
import mp.dottiewh.commands.noaliasCommands.tpacore.Tpa;
import mp.dottiewh.commands.noaliasCommands.tpacore.TpaAccept;
import mp.dottiewh.commands.noaliasCommands.tpacore.TpaCancel;
import mp.dottiewh.commands.noaliasCommands.tpacore.TpaDeny;
import mp.dottiewh.config.Config;
import mp.dottiewh.config.CustomConfig;
import mp.dottiewh.items.ItemConfig;
import mp.dottiewh.items.ItemMainCommand;
import mp.dottiewh.music.MusicConfig;
import mp.dottiewh.music.MusicMainCommand;
import mp.dottiewh.utils.U;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.literal;

public class BrigadierManager extends Commands{
    //==================
    public static void regNoAliasCommands(Plugin plugin){
        List<LiteralArgumentBuilder<CommandSourceStack>> listaLiterals = new LinkedList<>();
        CustomConfig cConfig = getRegCmdFileConfig();

        if(checkCmd(cConfig, "status")) listaLiterals.add(Status.getLiteralBuilder());
        if(checkCmd(cConfig, "repair")) listaLiterals.add(Repair.getLiteralBuilder("repair"));
        if(checkCmd(cConfig, "fix")) listaLiterals.add(Repair.getLiteralBuilder("fix"));
        if(checkCmd(cConfig, "jump")) listaLiterals.add(Jump.getLiteralBuilder());
        if(checkCmd(cConfig, "heal")) listaLiterals.add(Heal.getLiteralBuilder());
        if(checkCmd(cConfig, "gm")) listaLiterals.add(Gm.getLiteralBuilder());
        if(checkCmd(cConfig, "fly")) listaLiterals.add(Fly.getLiteralBuilder());
        if(checkCmd(cConfig, "feed")) listaLiterals.add(Feed.getLiteralBuilder());
        if(checkCmd(cConfig, "countdown")) listaLiterals.add(Countdown.getLiteralBuilder());
        if(checkCmd(cConfig, "coords")){
            listaLiterals.add(Coordenadas.getLiteralBuilder("coords"));
            listaLiterals.add(Coordenadas.getLiteralBuilder("coordenadas"));
        }
        if(checkCmd(cConfig, "tpa")){
            listaLiterals.add(Tpa.getLiteralBuilder());
            listaLiterals.add(TpaAccept.getLiteralBuilder());
            listaLiterals.add(TpaCancel.getLiteralBuilder());
            listaLiterals.add(TpaDeny.getLiteralBuilder());
        }
        if(checkCmd(cConfig, "playtime")) listaLiterals.add(PlayTime.getLiteralBuilder());
        if(checkCmd(cConfig, "back")) listaLiterals.add(BackCommand.getLiteralBuilder());
        //=========
        if(checkCmd(cConfig, "adminchat")){
            listaLiterals.add(AdminChat.getLiteralBuilder("adminchat"));
            listaLiterals.add(AdminChat.getLiteralBuilder("achat"));
            listaLiterals.add(AdminChat.getLiteralBuilder("ac"));
        }

        //
        for(LiteralArgumentBuilder<CommandSourceStack> litBuilder : listaLiterals){
            plugin.getLifecycleManager().registerEventHandler(
                    LifecycleEvents.COMMANDS,
                    event->{
                        event.registrar().register(
                                litBuilder.build(),
                                "No Alias DottUtils command."
                        );
                    }
            );
        }
    }
    //
    public static LiteralArgumentBuilder<CommandSourceStack> createAlias(Plugin pl, String name){
        return literal(name)
                .requires(ctx -> ctx.getSender().hasPermission("DottUtils.dottutils"))
                .then(Admin.getLiteralBuilder())
                .then(Reload.getLiteralBuilder())
                .then(Help.getLiteralBuilder())
                //
                .then(AdminChat.getLiteralBuilder("adminchat"))
                .then(AdminChat.getLiteralBuilder("achat"))
                .then(AdminChat.getLiteralBuilder("ac"))
                //
                .then(Whitelist.getLiteralBuilder())
                .then(Pvp.getLiteralBuilder())
                .then(NoFall.getLiteralBuilder())

                .then(TellRaw.getLiteralBuilder())
                //
                .then(ItemMainCommand.getLiteralBuilder())
                //
                .then(MusicMainCommand.getLiteralBuilder())
                //
                .then(CinematicMainCommand.getLiteralBuilder())
                //-------
                ;
    }

    public static void reloadBrigadier(){
        reloadBrigadierItems();
        reloadBrigadierMusics();
        reloadBrigadierCinematics();

    }
    public static void reloadBrigadierItems(){
        item_suggestions = (ctx, builder) ->{
            addSuggestion(builder, ItemConfig.getItems());
            return builder.buildFuture();
        };

        item_suggestions_files = (ctx, builder)->{
            String remaining = builder.getRemainingLowerCase();;
            for(String file : ItemConfig.getItemFiles()){
                if(file.toLowerCase().startsWith(remaining)) builder.suggest(file+".");
            }
            if("itemname".toLowerCase().startsWith(remaining)) builder.suggest("itemname");
            return builder.buildFuture();
        };
    }
    public static void reloadBrigadierMusics(){
        music_suggestions = (ctx, builder) ->{
            addSuggestion(builder, MusicConfig.getMusicList());
            return builder.buildFuture();
        };
    }
    public static void reloadBrigadierCinematics(){
        cinematics_suggestions = (ctx, builder) ->{
            addSuggestion(builder, CinematicsConfig.getCinematicsNameNotNull());

            return builder.buildFuture();
        };
    }
    private static void addSuggestion(SuggestionsBuilder builder, Collection<String> collection){
        String remaining = builder.getRemaining().toLowerCase();
        for(String id : collection){
            if (id.toLowerCase().startsWith(remaining)) {
                builder.suggest(id);
            }
        }
    }

    //------ reg cmds files--------
    public static boolean checkCmd(CustomConfig cConfig, String cmd){
        return checkCmd(cConfig, cmd, true);
    }
    public static boolean checkCmd(CustomConfig cConfig, String cmd, boolean def){
        Object got = cConfig.getConfig().get(cmd);
        if(got instanceof Boolean b) return b;

        //caso def
        cConfig.getConfig().set(cmd, def);
        cConfig.saveConfig();

        U.mensajeConsola("&eNo se ha encontrado el valor del comando a registrar "+cmd+", guardando def "+def+".");
        return def;
    }
    public static CustomConfig getRegCmdFileConfig(){
        CustomConfig registerConfig = new CustomConfig("reg_cmds.yml", null, DottUtils.getInstance(), true);
        registerConfig.registerConfig();
        return registerConfig;
    }
}
