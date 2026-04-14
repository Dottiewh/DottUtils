package mp.dottiewh.config;

import mp.dottiewh.DottUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class CustomConfig {
    private DottUtils plugin;
    private String fileName;
    private FileConfiguration fileConfiguration = null;
    private File file = null;
    private String folderName;
    private boolean newFile;

    public CustomConfig(String fileName, String folderName, DottUtils plugin, boolean newFile){
        this.fileName = fileName;
        this.folderName = folderName;
        this.plugin = plugin;
        this.newFile = newFile;
    }

    public String getPath(){
        return this.fileName;
    }

    public void registerConfig(){
        if(folderName != null){
            file = new File(plugin.getDataFolder() +File.separator + folderName,fileName);
        }else{
            file = new File(plugin.getDataFolder(), fileName);
        }

        if(!file.exists()){
            if(newFile){
                try{
                    file.createNewFile();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }else{
                if(folderName != null){
                    plugin.saveResource(folderName+File.separator+fileName, false);
                }else{
                    plugin.saveResource(fileName, false);
                }
            }

        }

        fileConfiguration = new YamlConfiguration();
        try {
            fileConfiguration.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public void saveConfig() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        if (fileConfiguration == null) {
            reloadConfig();
        }
        return fileConfiguration;
    }
    public File getFile(){
        return file;
    }

    public boolean reloadConfig() {
        if (fileConfiguration == null) {
            if(folderName != null){
                file = new File(plugin.getDataFolder() +File.separator + folderName, fileName);
            }else{
                file = new File(plugin.getDataFolder(), fileName);
            }

        }
        fileConfiguration = YamlConfiguration.loadConfiguration(file);

        if(file != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(file);
            fileConfiguration.setDefaults(defConfig);
        }
        return true;
    }

    //---------
    public static void registerDefFile(String fileName, String folderName, Plugin pl, boolean newFile){
        String path = folderName+"/"+fileName;
        registerDefFile(path, pl, newFile, false);
    }
    public static void registerDefFile(String fileName, String folderName, Plugin pl, boolean newFile, boolean replace){
        String path = folderName+"/"+fileName;
        registerDefFile(path, pl, newFile, false);
    }
    /**
     * @param path uses "/" as separator
     */
    public static void registerDefFile(String path, Plugin pl, boolean newFile){
        registerDefFile(path, pl, newFile, false);
    }
    /**
     * @param path uses "/" as separator
     */
    public static void registerDefFile(String path, Plugin pl, boolean newFile, boolean replace){
        File file;
        path = path.replace("/", File.separator);

        file = new File(pl.getDataFolder(), path);

        if(!file.exists()){
            if(newFile){
                try{
                    file.createNewFile();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }else{
                pl.saveResource(path, replace);
            }

        }
    }

    /**
     * ONLY USE IT, IF YOU'RE 100% PERCENT SURE THAT path = file path.
     * @param path / replaces with File.separator.
     */
    public static void registerDefFile(String path, File file, Plugin pl, boolean newFile, boolean replace){
        path = path.replace("/", File.separator);

        if(!file.exists()){
            if(newFile){
                try{
                    file.createNewFile();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }else{
                pl.saveResource(path, replace);
            }

        }
    }
    /**
     * @param path uses "/" as separator
     */
    public static File getFile(String path, Plugin pl){
        path = path.replace("/", File.separator);

        return new File(pl.getDataFolder(), path);
    }
}