package mp.dottiewh.utils;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import mp.dottiewh.DottUtils;
import mp.dottiewh.commands.aliasCommands.AdminChat;


public class DiscordUtils {

    public static boolean isAdminChannelSet(){
        String channelID = DottUtils.ymlConfig.getConfig().getString("discord_adminchat_channel");
        return channelID!=null && !channelID.equalsIgnoreCase("CHANNELID");
    }

    //______________
    /*
    A listener
     */
    public static void discordChatCoreFromDiscord(DiscordGuildMessageReceivedEvent event){
        String channelID = event.getChannel().getId();
        String expectedChannelID = DottUtils.ymlConfig.getConfig().getString("discord_adminchat_channel");

        if(!channelID.equalsIgnoreCase(expectedChannelID)) return;
        String name = event.getAuthor().getDisplayName();
        String msg = event.getMessage().getContentRaw();

        name = "&4{&cDiscord&4} &7"+name;
        AdminChat.sendACMsg(name, msg, true);
        AdminChat.consoleCore(name, msg);
    }
    public static void sendMsgToAdminChatDS(String name, String msg, boolean withPrefix){
        if(!DiscordUtils.isAdminChannelSet()) return;

        String channelID = DottUtils.ymlConfig.getConfig().getString("discord_adminchat_channel");
        if (channelID==null){
            U.mensajeDebugConsole("&cNo hay channel id setteado.");
            return;
        }

        TextChannel textChannel = DiscordSRV.getPlugin().getJda().getTextChannelById(channelID);
        if (textChannel==null){
            U.mensajeConsolaNP("&cTu id &e"+channelID+" &ces invalida!");
            return;
        }


        if(withPrefix){
            textChannel.sendMessage(name+" » "+msg).queue();
        }else{
            textChannel.sendMessage(msg).queue();
        }
    }
}
