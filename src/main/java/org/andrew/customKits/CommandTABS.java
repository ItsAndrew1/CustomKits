//Developed by: _ItsAndrew_
package org.andrew.customKits;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

//Adds each command's tabs
public class CommandTABS implements TabCompleter {
    CustomKits plugin;

    public CommandTABS(CustomKits plugin){
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args){
        if(command.getName().equalsIgnoreCase("kitconfig")){
            if(args.length == 1){
                return Arrays.asList("create", "manage", "delete", "reload", "help");
            }
            else if(args.length == 2 && args[0].equalsIgnoreCase("manage")){
                if(!plugin.getKits().getConfig().isConfigurationSection("kits")){
                    return Collections.emptyList();
                }
                Set<String> keys = plugin.getKits().getConfig().getConfigurationSection("kits").getKeys(false);
                return new ArrayList<>(keys);
            }
            else if(args.length == 2 && args[0].equalsIgnoreCase("delete")){
                if(!plugin.getKits().getConfig().isConfigurationSection("kits")){
                    return Collections.emptyList();
                }
                Set<String> keys = plugin.getKits().getConfig().getConfigurationSection("kits").getKeys(false);
                return new ArrayList<>(keys);
            }
            else if(args.length == 4 && args[2].equalsIgnoreCase("addenchant")){
                if(!plugin.getKits().getConfig().isConfigurationSection("kits."+args[1]+".items")){
                    return Collections.emptyList();
                }
                Set<String> keys = plugin.getKits().getConfig().getConfigurationSection("kits."+args[1]+".items").getKeys(false);
                return new ArrayList<>(keys);
            }
            else if(args.length == 4 && args[2].equalsIgnoreCase("removeenchant")){
                if(!plugin.getKits().getConfig().isConfigurationSection("kits."+args[1]+".items")){
                    return Collections.emptyList();
                }
                Set<String> keys = plugin.getKits().getConfig().getConfigurationSection("kits."+args[1]+".items").getKeys(false);
                return new ArrayList<>(keys);
            }
            else if(args.length == 3 && args[0].equalsIgnoreCase("manage")){
                return Arrays.asList("setperm", "setguiitem", "setguislot", "setglowing", "settitle", "additem", "removeitem", "addenchant", "removeenchant");
            }
            else if(args.length == 4 && args[2].equalsIgnoreCase("setglowing")){
                return Arrays.asList("true", "false");
            }
        }
        return new ArrayList<>();
    }
}
