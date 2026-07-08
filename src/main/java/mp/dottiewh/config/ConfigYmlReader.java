package mp.dottiewh.config;

import mp.dottiewh.DottUtils;
import mp.dottiewh.utils.U;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public class ConfigYmlReader {
    private final CustomConfig config;
    private final String prefix;

    /*
    Uses DottUtils prefix as default
     */
    public ConfigYmlReader(CustomConfig config) {
        this.config = config;
        this.prefix = DottUtils.prefix;
    }

    public ConfigYmlReader(CustomConfig config, String prefix) {
        this.config = config;
        this.prefix = prefix;
    }


    public int getInt(String path){
        return config.getConfig().getInt(path, -1);
    }
    public int getInt(String path, int def){
        int toGive = config.getConfig().getInt(path, -1);
        if (toGive==-1){
            config.getConfig().set(path, def);
            config.saveConfig();
            U.mensajeConsolaNP(prefix+"&cNo se ha detectado el path &f"+path+"&c en config. Regenerando con "+def+"...");
            return def;
        }
        return toGive;
    }
    public long getLong(String path){
        return config.getConfig().getLong(path, -1);
    }
    public long getLong(String path, long def){
        long toGive = config.getConfig().getLong(path, -1);
        if (toGive==-1){
            config.getConfig().set(path, def);
            config.saveConfig();
            U.mensajeConsolaNP(prefix+"&cNo se ha detectado el path &f"+path+"&c en config. Regenerando con "+def+"...");
            return def;
        }
        return toGive;
    }
    @Nullable
    public String getString(@NotNull String path){
        return config.getConfig().getString(path,  null);
    }
    @UnknownNullability
    public String getString(@NotNull String path, String def){
        String toGive = config.getConfig().getString(path, null);
        if (toGive==null){
            config.getConfig().set(path, def);
            config.saveConfig();
            U.mensajeConsolaNP(prefix+"&cNo se ha detectado el path &f"+path+"&c en config. Regenerando con "+def+"...");
            return def;
        }
        return toGive;
    }
    public boolean getBoolean(@NotNull String path){
        return config.getConfig().getBoolean(path,  false);
    }
    public boolean getBoolean(@NotNull String path, boolean def){
        Object got = config.getConfig().get(path);
        boolean success = (got instanceof Boolean);

        if (!success){
            config.getConfig().set(path, def);
            config.saveConfig();
            U.mensajeConsolaNP(prefix+"&cNo se ha detectado el path &f"+path+"&c en config. Regenerando con "+def+"...");
            return def;
        }
        return (Boolean) got;
    }

    //===============
    public CustomConfig getConfig() {
        return config;
    }
    public String getPrefix() {
        return prefix;
    }
}
