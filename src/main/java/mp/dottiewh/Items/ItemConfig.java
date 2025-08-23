package mp.dottiewh.Items;

import mp.dottiewh.DottUtils;
import mp.dottiewh.aliasCommands.AdminChat;
import mp.dottiewh.config.CustomConfig;

import java.util.ArrayList;
import java.util.List;

public class ItemConfig {
    private static CustomConfig config;
    private static CustomConfig configItem;


    public static void itemConfigInit(){
        config = DottUtils.getRegisteredConfig();
        configItem = DottUtils.getRegisteredItemConfig();
    }
}
