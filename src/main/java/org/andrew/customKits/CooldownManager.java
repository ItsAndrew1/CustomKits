//Developed by _ItsAndrew_
package org.andrew.customKits;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CooldownManager{
    private final CustomKits plugin;

    public CooldownManager(CustomKits plugin){
        this.plugin = plugin;
    }

    //Starts the cooldown when a player gets a kit
    public void startCooldown(Player player){
        FileConfiguration playerData = plugin.getPlayerData().getConfig();
        boolean isCooldownForever = plugin.getConfig().getBoolean("kits-cooldown-forever");

        //If the option of staff having no cooldowns is true
        if(plugin.getConfig().getBoolean("kits-cooldown-staff")){
            if(player.hasPermission("kits.admin")){
                playerData.set("players."+player.getName()+".cooldown", 0);
                plugin.getPlayerData().saveConfig();
                return;
            }
        }

        //If the cooldown is forever
        if(isCooldownForever){
            playerData.set("players."+player.getName()+".gotKitAlready", true);
            return;
        }

        long cooldownSecs = parseDuration(Objects.requireNonNull(plugin.getConfig().getString("kits-cooldown")));
        long expiresAt = System.currentTimeMillis() / 1000 + cooldownSecs;

        playerData.set("players."+player.getName()+".cooldown", expiresAt);
        plugin.getPlayerData().saveConfig();
    }

    //Checks whether the player has cooldown
    public boolean hasCooldown(Player player){
        plugin.getPlayerData().reloadConfig();
        FileConfiguration playerData = plugin.getPlayerData().getConfig();

        long now = System.currentTimeMillis() / 1000;
        long expiresAt = playerData.getLong("players."+player.getName()+".cooldown");

        return now < expiresAt;
    }

    //Returns the remaining cooldown
    public long getRemainingCooldown(Player player){
        plugin.getPlayerData().reloadConfig();
        FileConfiguration playerData = plugin.getPlayerData().getConfig();

        long now = System.currentTimeMillis() / 1000;
        long expiresAt = playerData.getLong("players."+player.getName()+".cooldown");

        long remaining = expiresAt - now;
        return Math.max(0, remaining);
    }

    public long parseDuration(String input){
        long seconds = 0;
        Matcher m = Pattern.compile("(\\d+)([dhms])").matcher(input.toLowerCase());

        while(m.find()){
            int value =Integer.parseInt(m.group(1));
            switch(m.group(2)){
                case "d" -> seconds += value * 86400L;
                case "h" -> seconds += value * 3600L;
                case "m" -> seconds += value * 60L;
                case "s" -> seconds += value;
            }
        }
        return seconds;
    }
}
