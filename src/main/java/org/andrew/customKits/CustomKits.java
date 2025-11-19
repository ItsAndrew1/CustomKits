//Developed by: _ItsAndrew_
package org.andrew.customKits;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomKits extends JavaPlugin {
    private YMLfiles kits;
    private YMLfiles messages;
    private YMLfiles playerData;
    private int invSize;
    KitsGUI GuiManager;
    CooldownManager CooldownManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        //Creating the YML files
        kits = new YMLfiles(this, "kits.yml");
        messages = new YMLfiles(this, "messages.yml");
        playerData = new YMLfiles(this, "playerdata.yml");

        invSize = getConfig().getInt("gui-rows") * 9;
        GuiManager = new KitsGUI(this);
        CooldownManager = new CooldownManager(this);

        //Setting up the commands and the tab completer
        getCommand("kitconfig").setExecutor(new CommandManager(this));
        getCommand("kitconfig").setTabCompleter(new CommandTABS(this));
        getCommand("kits").setExecutor(new CommandManager(this));

        //Setting up the GUI events
        getServer().getPluginManager().registerEvents(new KitsGUI(this), this);

        //Check any errors from the .yml files
        if(getConfig().getInt("gui-rows") < 1 || getConfig().getInt("gui-rows") > 6){
            Bukkit.getLogger().warning("[CK] ERROR! gui-rows IN CONFIG.YML is INVALID!");
        }
        if(getConfig().getBoolean("exit.item-toggle")){
            if(getConfig().getInt("exit-item.slot") < 1 || getConfig().getInt("exit-item.slot") > invSize){
                Bukkit.getLogger().warning("[CK] The slot of exit-item is invalid!");
            }
        }
    }

    @Override
    public void onDisable() {
        //Saving each config file
        getKits().saveConfig();
        getMessages().saveConfig();
        getPlayerData().saveConfig();
        saveConfig();

        Bukkit.getLogger().info("[CK] CustomKits shut down successfully!");
    }

    //Getters
    public YMLfiles getKits(){
        return kits;
    }
    public YMLfiles getMessages(){
        return messages;
    }
    public YMLfiles getPlayerData(){
        return playerData;
    }
    public KitsGUI getGuiManager(){
        return GuiManager;
    }
    public CooldownManager getCooldownManager(){
        return CooldownManager;
    }
    public int getInvSize(){
        return invSize;
    }
}
