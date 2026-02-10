//Developed by: _ItsAndrew_
package org.andrew.customKits;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomKits extends JavaPlugin {
    private YMLfiles kits;
    private YMLfiles playerData;
    private int invSize;
    private KitsGUI GuiManager;
    private CooldownManager CooldownManager;
    private GivingKitsTask giveKitsTask;

    //Defining the permission object
    private static Permission permissions;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        //Creating the YML files
        kits = new YMLfiles(this, "kits.yml");
        playerData = new YMLfiles(this, "playerdata.yml");

        invSize = getConfig().getInt("gui-rows") * 9;
        GuiManager = new KitsGUI(this);
        CooldownManager = new CooldownManager(this);
        giveKitsTask = new GivingKitsTask(this);

        //Setting up the commands and the tab completer
        getCommand("kitconfig").setExecutor(new CommandManager(this));
        getCommand("kitconfig").setTabCompleter(new CommandTABS(this));
        getCommand("kits").setExecutor(new CommandManager(this));

        //Setting up the GUI events
        getServer().getPluginManager().registerEvents(GuiManager, this);

        //Check any errors from the .yml files
        if(getConfig().getInt("gui-rows") < 1 || getConfig().getInt("gui-rows") > 6){
            Bukkit.getLogger().warning("[CUSTOMKITS] ERROR! gui-rows IN CONFIG.YML is INVALID!");
        }
        if(getConfig().getBoolean("exit.item-toggle")){
            if(getConfig().getInt("exit-item.slot") < 1 || getConfig().getInt("exit-item.slot") > invSize){
                Bukkit.getLogger().warning("[CUSTOMKITS] The slot of exit-item is invalid!");
            }
        }

        //Sets the permission
        setupPermissions();
    }

    @Override
    public void onDisable() {
        //Saving each config file
        getKits().saveConfig();
        getPlayerData().saveConfig();
        saveConfig();

        Bukkit.getLogger().info("[CUSTOMKITS] CustomKits shut down successfully!");
    }

    private void setupPermissions(){
        if(getServer().getPluginManager().getPlugin("Vault") != null){
            getLogger().warning("[CUSTOMKITS] Vault plugin not detected. The permissions won't work normally!");
            return;
        }

        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        permissions = permissionProvider.getProvider();
    }

    //Getter for permissions object
    public Permission getPermissions() {
        return permissions;
    }

    //Getters
    public YMLfiles getKits(){
        return kits;
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
    public GivingKitsTask getGiveKitsTask(){
        return giveKitsTask;
    }
    public int getInvSize(){
        return invSize;
    }
}
