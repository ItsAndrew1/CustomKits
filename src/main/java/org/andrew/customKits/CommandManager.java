//Developed by: _ItsAndrew_
package org.andrew.customKits;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

//Main class for every command
public class CommandManager implements CommandExecutor {
    private final CustomKits plugin;

    public CommandManager(CustomKits plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args){
        FileConfiguration messages = plugin.getMessages().getConfig();
        FileConfiguration kits = plugin.getKits().getConfig();
        Player player = (Player) sender;

        // /kitconfig (or /kc) command
        if(command.getName().equalsIgnoreCase("kitconfig")) {
            if(!sender.hasPermission("kits.admin")){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("no-permission")));
                return true;
            }

            if(args.length == 0){
                Bukkit.getLogger().info("[CK] Usage: /kitconfig <create | delete | list | manage | reload | help>");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("no-args")));
                return true;
            }

            switch(args[0]){
                case "create":
                    if(args.length < 2){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cUsage: &c&l/kitconfig create <name>"));
                        return true;
                    }
                    else{
                        kits.set("kits."+args[1]+".title", "");
                        kits.set("kits."+args[1]+".enchantglow", "");
                        kits.set("kits."+args[1]+".gui-item", "");
                        kits.set("kits."+args[1]+".gui-slot", "");
                        kits.set("kits."+args[1]+".lore", "");
                        kits.set("kits."+args[1]+".permission", "");
                        kits.set("kits."+args[1]+".items", "");
                        plugin.getKits().saveConfig();

                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &aKit &l"+args[1]+" &asaved to &lkits.yml&a!"));
                        Bukkit.getLogger().info("[CK] Kit "+args[1]+" saved to kits.yml!");
                    }

                    break;

                case "delete":
                    if(!kits.isConfigurationSection("kits")){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cYou don't have any kits configured!"));
                        Bukkit.getLogger().info("[CK] You don't have any kits configured.");
                        return true;
                    }
                    else if(args.length < 2){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cUsage: &c&l/kitconfig delete <name>"));
                        Bukkit.getLogger().info("[CK] Usage: /kitconfig delete <name>");
                        return true;
                    }
                    else{
                        String kitName = args[1];
                        if(Objects.requireNonNull(kits.getConfigurationSection("kits")).getKeys(false).contains(kitName)){
                            kits.set("kits."+kitName, null);
                            plugin.getKits().saveConfig();

                            Bukkit.getLogger().info("[CK] Kit "+kitName+" successfully deleted.");
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &aKit &l"+kitName+" &asuccessfully deleted."));
                        }
                        else{
                            Bukkit.getLogger().info("[CK] Kit "+kitName+" doesn't exist.");
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cKit &l"+kitName+" &cdoesn't exist."));
                        }
                    }
                    break;

                case "help":
                    List<String> helpMessage = messages.getStringList("help-message");
                    for(String line : helpMessage){
                        String coloredLine = ChatColor.translateAlternateColorCodes('&', line);
                        sender.sendMessage(coloredLine);
                        Bukkit.getLogger().info(line);
                    }
                    break;

                case "reload":
                    plugin.getKits().reloadConfig();
                    plugin.getMessages().reloadConfig();
                    plugin.reloadConfig();

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &aCustomKits reloaded successfully."));
                    Bukkit.getLogger().info("[CK] CustomKits reloaded successfully.");
                    break;

                case "manage":
                    if(!kits.isConfigurationSection("kits")){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cYou have no kits configured!"));
                        Bukkit.getLogger().info("[CK] You don't have any kits configured!");
                        return true;
                    }
                    if(args.length < 3){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cUsage: &l/kitconfig manage <name> ..."));
                        Bukkit.getLogger().info("[CK] Usage: /kitconfig manage <name> ...");
                        return true;
                    }

                    String kitName = args[1];
                    String path = "kits."+kitName;
                    if(!Objects.requireNonNull(kits.getConfigurationSection("kits").getKeys(false)).contains(kitName)){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cKit &l"+kitName+" &cdoesn't exist!"));
                        Bukkit.getLogger().info("[CK] Kit "+kitName+" doesn't exist!");
                        return true;
                    }

                    switch(args[2]){
                        case "setperm":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix")+" &cUsage: &l/kc manage ... setperm <perm>"));
                                return true;
                            }

                            String permissionName = args[3];
                            kits.set(path + ".permission", null);
                            kits.set(path+".permission", permissionName);
                            plugin.getKits().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &aPermission &l" + permissionName + " &aadded to kit &l"+ kitName+"&a."));
                            Bukkit.getLogger().info("[CK] Permission "+permissionName+" added to kit "+kitName+".");
                            break;

                        case "setguiitem":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cUsage: &l/kc manage ... setguiitem <item>"));
                                return true;
                            }

                            String item = args[3];
                            Material materialToSet = Material.matchMaterial(item);
                            if(materialToSet == null){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cItem &l"+item+" &cdoes not exist in the game!"));
                                return true;
                            }

                            kits.set(path + ".gui-item", null);
                            kits.set(path + ".gui-item", item);
                            plugin.getKits().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &aGUI Item saved successfully for kit &l"+kitName+"&a!"));
                            Bukkit.getLogger().info("[CK] GUI Item &l"+item+" &asaved successfully for kit "+kitName+"!");
                            break;

                        case "setguislot":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cUsage: &l/kc manage ... setguislot <item>"));
                                return true;
                            }

                            int slot;
                            try{
                                slot = Integer.parseInt(args[3]);
                                if(slot < 1 || slot > plugin.getInvSize()){
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cThe slot must be between &l1 &cand &l"+plugin.getInvSize()+"&c!"));
                                    return true;
                                }
                            }  catch (NumberFormatException e){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cThe number is &linvalid &c!"));
                                return true;
                            }

                            kits.set(path+".gui-slot", slot);
                            plugin.getKits().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &aSaved slot &l"+slot+"&a for kit &l"+kitName+"&a!"));
                            Bukkit.getLogger().info("[CK] Saved slot "+slot+" for kit "+kitName+"!");
                            break;

                        case "setglowing":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cUsage: &l/kc manage ... setglowing <true/false>"));
                                return true;
                            }

                            String value = args[3].toLowerCase();
                            if(!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cThe value must be &ltrue/false&c!"));
                                return true;
                            }

                            kits.set("kits."+kitName+".enchantglow", value);
                            plugin.getKits().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &aValue &l"+value+" &afor &lEnchantGlow &asaved for kit &l"+kitName+"&a!"));
                            Bukkit.getLogger().info("[CK] Value "+value+" for EnchantGlow saved for kit "+kitName+"!");
                            break;

                        case "settitle":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cUsage: &l/kc manage ... settitle <title>"));
                                return true;
                            }

                            String title = args[3];
                            kits.set("kits."+kitName+".title", title);
                            plugin.getKits().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix")+" &aTitle &r"+title+" &asaved for kit &l"+kitName+"&a!"));
                            Bukkit.getLogger().info("[CK] Title "+title+" saved for kit "+kitName+"!");
                            break;

                        case "additem":
                            if(args.length < 5){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cUsage: &l/kitconfig manage ... additem <name> <q>"));
                                return true;
                            }
                            String rawItem = args[3].toLowerCase();
                            Material materialToAdd = Material.matchMaterial(rawItem);
                            int quantity;

                            try{
                                quantity = Integer.parseInt(args[4]);
                                if(quantity < 1){
                                    Bukkit.getLogger().info("[CK] The quantity cannot be lower than 1!");
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cThe quantity cannot be lower than 1!"));
                                    return true;
                                }
                            } catch (NumberFormatException e){
                                Bukkit.getLogger().info("[CK] The quantity must be a number!");
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cThe quantity must be a number!"));
                                return true;
                            }

                            if(materialToAdd == null) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cItem &l" + rawItem + " &cdoes not exist in the game!"));
                                return true;
                            }
                            String pathToAdd = path+".items."+rawItem+".quantity";
                            String pathToAdd2 = path+".items."+rawItem+".enchantments";
                            String pathToAdd3 = path+".items."+rawItem+".material";
                            kits.set(pathToAdd, quantity);
                            kits.set(pathToAdd2, "");
                            kits.set(pathToAdd3, rawItem);
                            plugin.getKits().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &aItem &l"+rawItem+" &awith quantity &l"+quantity+" &aadded to kit &l"+kitName+"&a!"));
                            Bukkit.getLogger().info("[CK] Item "+rawItem+" with quantity "+quantity+" added to kit "+kitName+"!");
                            break;

                        case "removeitem":
                            if(!kits.isConfigurationSection("kits."+kitName+".items")){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cYou have no items in kit &l"+kitName+"&c!"));
                                return true;
                            }
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cUsage: &l/kitconfig manage ... removeitem <name"));
                                return true;
                            }

                            String itemToDelete = args[3].toLowerCase();
                            Material materialToDelete = Material.matchMaterial(itemToDelete);
                            String pathToDelete = "kits."+kitName+".items."+itemToDelete;

                            if(materialToDelete == null){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cItem &l"+itemToDelete+" &cdoes not exist in the game!"));
                                return true;
                            }
                            if(Objects.requireNonNull(kits.getConfigurationSection(path+".items").getKeys(false).contains(itemToDelete))){
                                kits.set(pathToDelete, null);
                                plugin.getKits().saveConfig();

                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &aItem &l"+itemToDelete+" &adeleted from kit &l"+kitName+"&a!"));
                                Bukkit.getLogger().info(("[CK] Item "+itemToDelete+" deleted from kit "+kitName+"!"));
                            }
                            else{
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cItem &l"+itemToDelete+" &cdoes not exist in kit &l"+kitName+"&c!"));
                                Bukkit.getLogger().info("[CK] Item "+itemToDelete+" does not exist in kit "+kitName+"!");
                            }

                            break;

                        case "addenchant":
                            if(!kits.isConfigurationSection(path+".items")){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cYou have no items in kit &l" + kitName+"&c!"));
                                return true;
                            }
                            if(args.length < 6){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cUsage: &l/kc manage <kit> addenchant <item> <enchant> <q>"));
                                return true;
                            }

                            String itemToAddEnchant = args[3];
                            if(Objects.requireNonNull(kits.getConfigurationSection(path+".items").getKeys(false).contains(itemToAddEnchant))){
                                String enchantment = args[4].toUpperCase(); //Example: Sharpness, Mending, Unbreaking etc.
                                Enchantment enchant = Enchantment.getByName(enchantment);
                                if(enchant == null){
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cEnchant &l"+enchantment+" &cdoes not exist!"));
                                    return true;
                                }

                                int enchantLevel;
                                String pathToAddEnchant = path+".items."+itemToAddEnchant+".enchantments."+enchantment;
                                try{
                                    enchantLevel = Integer.parseInt(args[5]);

                                    if(enchantLevel < 0){
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cThe level must be at least &l0&c!"));
                                        return true;
                                    }

                                    else if(enchant.getMaxLevel() == 1){
                                        kits.set(pathToAddEnchant, enchantLevel);
                                        plugin.getKits().saveConfig();

                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &aEnchantment &l"+enchant.getKey().getKey()+" &aadded to item &l"+itemToAddEnchant+"&a!"));
                                        return true;
                                    }
                                    else if(enchantLevel < enchant.getStartLevel() || enchantLevel > enchant.getMaxLevel()){
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cThe level for enchant &l"+enchant.getKey().getKey()+" &cmust be between &l"+enchant.getStartLevel()+" &cand &l"+enchant.getMaxLevel()+"&c!"));
                                        return true;
                                    }
                                }catch (NumberFormatException e){
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cThe level must be a number!"));
                                    return true;
                                }

                                kits.set(pathToAddEnchant, enchantLevel);
                                plugin.getKits().saveConfig();

                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &aEnchantment &l"+enchant.getKey().getKey()+" &aadded to item &l"+itemToAddEnchant+"&a!"));
                                Bukkit.getLogger().info("[CK] Enchantment "+enchant.getKey().getKey()+" added to item "+itemToAddEnchant+"!");
                            }
                            else{
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cItem &l"+itemToAddEnchant+" &cdoes not exist in kit &l"+kitName+"&c!"));
                                return true;
                            }

                            break;

                        case "removeenchant":
                            if(!kits.isConfigurationSection(path+".items")){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cYou have no items in kit &l"+kitName+"&c!"));
                                return true;
                            }
                            if(args.length < 5){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cUsage: &l/kc manage <kit> removeenchant <item> <enchant>"));
                                return true;
                            }

                            String itemToRemoveEnchant = args[3];
                            if(Objects.requireNonNull(kits.getConfigurationSection(path+".items").getKeys(false).contains(itemToRemoveEnchant))){
                                String enchantToDelete = args[4].toUpperCase();

                                if(Objects.requireNonNull(kits.getConfigurationSection(path+".items."+itemToRemoveEnchant+".enchantments").getKeys(false).contains(enchantToDelete))){
                                    kits.set(path+".items."+itemToRemoveEnchant+".enchantments."+enchantToDelete, null);
                                    plugin.getKits().saveConfig();

                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &aEnchant &l"+enchantToDelete+" &aremoved from item &l"+itemToRemoveEnchant+"&a!"));
                                    Bukkit.getLogger().info("[CK] Enchant "+enchantToDelete+" removed from item "+itemToRemoveEnchant+"!");
                                }
                                else{
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cEnchant &l"+enchantToDelete+" &cdoes not exist for the item &l"+itemToRemoveEnchant));
                                    return true;
                                }
                            }
                            else{
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cItem &l"+itemToRemoveEnchant+" &cdoes not exist in kit &l"+kitName+"&c!"));
                                return true;
                            }
                            break;

                        default:
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cUnkown command. Use &l/kitconfighelp &cfor info."));
                            break;
                    }

                    break;

                default:
                    Bukkit.getLogger().info("[CK] Unknown command. Use /kitconfig help for info.");
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix") + " &cUnkown command. Use &l/kitconfighelp &cfor info."));
                    break;
            }

            return true;
        }

        //The /kits command (I know it's pretty small :) )
        if(command.getName().equalsIgnoreCase("kits")){
            plugin.getGuiManager().showGUI(player);
            return true;
        }

        return false;
    }
}
