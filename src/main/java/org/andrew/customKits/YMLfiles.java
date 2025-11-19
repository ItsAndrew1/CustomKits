//Developed by _ItsAndrew_
package org.andrew.customKits;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

//Helps me to create, get, save and reload each .yml file
public class YMLfiles {
    private final CustomKits plugin;
    private final String fileName;
    private File file;
    private FileConfiguration config;

    public YMLfiles(CustomKits plugin, String fileName){
        this.plugin = plugin;
        this.fileName =fileName.endsWith(".yml") ? fileName : fileName + ".yml";
        createFile();
    }

    public void createFile(){
        file = new File(plugin.getDataFolder(), fileName);
        if(!file.exists()){
            plugin.saveResource(fileName, false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig(){
        return config;
    }

    public void saveConfig(){
        try{
            config.save(file);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void reloadConfig(){
        config = YamlConfiguration.loadConfiguration(file);
    }
}
