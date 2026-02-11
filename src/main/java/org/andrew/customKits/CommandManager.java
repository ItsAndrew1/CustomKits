//Developed by: _ItsAndrew_
package org.andrew.customKits;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
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
        FileConfiguration kits = plugin.getKits().getConfig();
        ConfigurationSection kitsSection = kits.getConfigurationSection("kits");

        String chatPrefix = plugin.getConfig().getString("prefix");
        Player player = (Player) sender;

        //Sounds
        Sound good = Registry.SOUNDS.get(NamespacedKey.minecraft("entity.player.levelup"));
        Sound invalid = Registry.SOUNDS.get(NamespacedKey.minecraft("entity.enderman.teleport"));

        if(command.getName().equalsIgnoreCase("kitconfig")) {
            if(!sender.hasPermission("kits.admin")){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("no-permission-message")));
                player.playSound(player.getLocation(), invalid, 1f, 1f);
                return true;
            }

            if(args.length == 0){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cUsage: /kitconfig <create | delete | help | reload | manage"));
                player.playSound(player.getLocation(), invalid, 1f, 1f);
                return true;
            }

            switch(args[0]){
                case "create":
                    if(args.length < 2){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cUsage: &c&l/kitconfig create <name>"));
                        player.playSound(player.getLocation(), invalid, 1f, 1f);
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

                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &aKit &l"+args[1]+" &asaved to &lkits.yml&a!"));
                        player.playSound(player.getLocation(), good, 1f, 1.4f);
                    }

                    break;

                case "delete":
                    //Checking if there are any kits configured
                    if(kitsSection == null || kitsSection.getKeys(false).isEmpty()){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cYou don't have any kits configured!"));
                        player.playSound(player.getLocation(), invalid, 1f, 1f);
                        return true;
                    }
                    else if(args.length < 2){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cUsage: &c&l/kitconfig delete <name>"));
                        player.playSound(player.getLocation(), invalid, 1f, 1f);
                        return true;
                    }
                    else{
                        String kitName = args[1];
                        if(Objects.requireNonNull(kits.getConfigurationSection("kits")).getKeys(false).contains(kitName)){
                            kits.set("kits."+kitName, null);
                            plugin.getKits().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &aKit &l"+kitName+" &asuccessfully deleted."));
                            player.playSound(player.getLocation(), good, 1f, 1.4f);
                        }
                        else{
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cKit &l"+kitName+" &cdoesn't exist."));
                            player.playSound(player.getLocation(), invalid, 1f, 1f);
                        }
                    }
                    break;

                case "help":
                    List<String> helpMessage = plugin.getConfig().getStringList("help-message");
                    for(String line : helpMessage){
                        String coloredLine = ChatColor.translateAlternateColorCodes('&', line);
                        sender.sendMessage(coloredLine);
                    }
                    player.playSound(player.getLocation(), good, 1f, 1.4f);
                    break;

                case "reload":
                    plugin.getKits().reloadConfig();
                    plugin.reloadConfig();

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &aCustomKits reloaded successfully."));
                    player.playSound(player.getLocation(), good, 1f, 1.4f);
                    break;

                case "manage":
                    //Checking if there are kits configured
                    if(kitsSection == null || kitsSection.getKeys(false).isEmpty()){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cYou have no kits configured!"));
                        player.playSound(player.getLocation(), invalid, 1f, 1f);
                        return true;
                    }
                    if(args.length < 3){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cUsage: &l/kitconfig manage <name> ..."));
                        player.playSound(player.getLocation(), invalid, 1f, 1f);
                        return true;
                    }

                    String kitName = args[1];
                    String path = "kits."+kitName;
                    if(!Objects.requireNonNull(kits.getConfigurationSection("kits").getKeys(false)).contains(kitName)){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cKit &l"+kitName+" &cdoesn't exist!"));
                        player.playSound(player.getLocation(), invalid, 1f, 1f);
                        return true;
                    }

                    ConfigurationSection kitItemsSection = kits.getConfigurationSection(path+".items");
                    switch(args[2]){
                        case "setperm":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix+" &cUsage: &l/kc manage ... setperm <perm>"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }

                            String permissionName = args[3];
                            kits.set(path + ".permission", null);
                            kits.set(path+".permission", permissionName);
                            plugin.getKits().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &aPermission &l" + permissionName + " &aadded to kit &l"+ kitName+"&a."));
                            player.playSound(player.getLocation(), good, 1f, 1.4f);
                            break;

                        case "setguiitem":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cUsage: &l/kc manage ... setguiitem <item>"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }

                            String item = args[3];
                            Material materialToSet = Material.matchMaterial(item);
                            if(materialToSet == null){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cItem &l"+item+" &cdoes not exist in the game!"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }

                            kits.set(path + ".gui-item", null);
                            kits.set(path + ".gui-item", item);
                            plugin.getKits().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix+ " &aGUI Item saved successfully for kit &l"+kitName+"&a!"));
                            player.playSound(player.getLocation(), good, 1f, 1.4f);
                            break;

                        case "setguislot":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cUsage: &l/kc manage ... setguislot <item>"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }

                            int slot;
                            try{
                                slot = Integer.parseInt(args[3]);
                                if(slot < 1 || slot > plugin.getInvSize()){
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cThe slot must be between &l1 &cand &l"+plugin.getInvSize()+"&c!"));
                                    player.playSound(player.getLocation(), invalid, 1f, 1f);
                                    return true;
                                }
                            }  catch (NumberFormatException e){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cThe number is &linvalid &c!"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }

                            kits.set(path+".gui-slot", slot);
                            plugin.getKits().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix+ " &aSaved slot &l"+slot+"&a for kit &l"+kitName+"&a!"));
                            player.playSound(player.getLocation(), good, 1f, 1.4f);
                            break;

                        case "setglowing":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cUsage: &l/kc manage ... setglowing <true/false>"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }

                            String value = args[3].toLowerCase();
                            if(!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cThe value must be &ltrue/false&c!"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }

                            boolean boolValue = Boolean.parseBoolean(value);
                            kits.set("kits."+kitName+".enchantglow", boolValue);
                            plugin.getKits().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix+ " &aValue &l"+value+" &afor &lEnchantGlow &asaved for kit &l"+kitName+"&a!"));
                            player.playSound(player.getLocation(), good, 1f, 1.4f);
                            break;

                        case "settitle":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cUsage: &l/kc manage ... settitle <title>"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }

                            String title = args[3];
                            kits.set("kits."+kitName+".title", title);
                            plugin.getKits().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix+" &aTitle &r"+title+" &asaved for kit &l"+kitName+"&a!"));
                            player.playSound(player.getLocation(), good, 1f, 1.4f);
                            break;

                        case "additem":
                            if(args.length < 5){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cUsage: &l/kitconfig manage ... additem <name> <q>"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }
                            String rawItem = args[3].toLowerCase();
                            Material materialToAdd = Material.matchMaterial(rawItem);
                            int quantity;

                            try{
                                quantity = Integer.parseInt(args[4]);
                                if(quantity < 1){
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cThe quantity cannot be lower than 1!"));
                                    player.playSound(player.getLocation(), invalid, 1f, 1f);
                                    return true;
                                }
                            } catch (NumberFormatException e){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cThe quantity must be a number!"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }

                            if(materialToAdd == null) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cItem &l" + rawItem + " &cdoes not exist in the game!"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }
                            String pathToAdd = path+".items."+rawItem+".quantity";
                            String pathToAdd2 = path+".items."+rawItem+".enchantments";
                            String pathToAdd3 = path+".items."+rawItem+".material";
                            kits.set(pathToAdd, quantity);
                            kits.set(pathToAdd2, "");
                            kits.set(pathToAdd3, rawItem);
                            plugin.getKits().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &aItem &l"+rawItem+" &awith quantity &l"+quantity+" &aadded to kit &l"+kitName+"&a!"));
                            player.playSound(player.getLocation(), good, 1f, 1.4f);
                            break;

                        case "removeitem":
                            //Check if there are any items in the kit
                            if(kitItemsSection == null || kitItemsSection.getKeys(false).isEmpty()){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cYou have no items in kit &l"+kitName+"&c!"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cUsage: &l/kitconfig manage ... removeitem <name"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }

                            String itemToDelete = args[3].toLowerCase();
                            Material materialToDelete = Material.matchMaterial(itemToDelete);
                            String pathToDelete = "kits."+kitName+".items."+itemToDelete;

                            if(materialToDelete == null){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cItem &l"+itemToDelete+" &cdoes not exist in the game!"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }
                            if(Objects.requireNonNull(kits.getConfigurationSection(path+".items").getKeys(false).contains(itemToDelete))){
                                kits.set(pathToDelete, null);
                                plugin.getKits().saveConfig();

                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &aItem &l"+itemToDelete+" &adeleted from kit &l"+kitName+"&a!"));
                                player.playSound(player.getLocation(), good, 1f, 1.4f);
                            }
                            else{
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cItem &l"+itemToDelete+" &cdoes not exist in kit &l"+kitName+"&c!"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }
                            break;

                        case "addenchant":
                            //Check if there are items in the kit
                            if(kitItemsSection == null || kitItemsSection.getKeys(false).isEmpty()){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cYou have no items in kit &l" + kitName+"&c!"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }
                            if(args.length < 6){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cUsage: &l/kc manage <kit> addenchant <item> <enchant> <q>"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }

                            String itemToAddEnchant = args[3];
                            if(Objects.requireNonNull(kits.getConfigurationSection(path+".items").getKeys(false).contains(itemToAddEnchant))){
                                String enchantment = args[4].toUpperCase(); //Example: Sharpness, Mending, Unbreaking etc.
                                Enchantment enchant = Enchantment.getByName(enchantment);
                                if(enchant == null){
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cEnchant &l"+enchantment+" &cdoes not exist!"));
                                    player.playSound(player.getLocation(), invalid, 1f, 1f);
                                    return true;
                                }

                                int enchantLevel;
                                String pathToAddEnchant = path+".items."+itemToAddEnchant+".enchantments."+enchantment;
                                try{
                                    enchantLevel = Integer.parseInt(args[5]);

                                    if(enchantLevel < 0){
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cThe level must be at least &l0&c!"));
                                        player.playSound(player.getLocation(), invalid, 1f, 1f);
                                        return true;
                                    }

                                    else if(enchant.getMaxLevel() == 1){
                                        kits.set(pathToAddEnchant, enchantLevel);
                                        plugin.getKits().saveConfig();

                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &aEnchantment &l"+enchant.getKey().getKey()+" &aadded to item &l"+itemToAddEnchant+"&a!"));
                                        player.playSound(player.getLocation(), good, 1f, 1.4f);
                                        return true;
                                    }
                                    else if(enchantLevel < enchant.getStartLevel() || enchantLevel > enchant.getMaxLevel()){
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cThe level for enchant &l"+enchant.getKey().getKey()+" &cmust be between &l"+enchant.getStartLevel()+" &cand &l"+enchant.getMaxLevel()+"&c!"));
                                        player.playSound(player.getLocation(), invalid, 1f, 1f);
                                        return true;
                                    }
                                }catch (NumberFormatException e){
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cThe level must be a number!"));
                                    player.playSound(player.getLocation(), invalid, 1f, 1f);
                                    return true;
                                }

                                kits.set(pathToAddEnchant, enchantLevel);
                                plugin.getKits().saveConfig();

                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &aEnchantment &l"+enchant.getKey().getKey()+" &aadded to item &l"+itemToAddEnchant+"&a!"));
                                player.playSound(player.getLocation(), good, 1f, 1.4f);
                            }
                            else{
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cItem &l"+itemToAddEnchant+" &cdoes not exist in kit &l"+kitName+"&c!"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }

                            break;

                        case "removeenchant":
                            //Check if there are items in the kit
                            if(kitItemsSection == null || kitItemsSection.getKeys(false).isEmpty()){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cYou have no items in kit &l"+kitName+"&c!"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }
                            if(args.length < 5){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cUsage: &l/kc manage <kit> removeenchant <item> <enchant>"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }

                            String itemToRemoveEnchant = args[3];
                            if(Objects.requireNonNull(kits.getConfigurationSection(path+".items").getKeys(false).contains(itemToRemoveEnchant))){
                                String enchantToDelete = args[4].toUpperCase();

                                if(Objects.requireNonNull(kits.getConfigurationSection(path+".items."+itemToRemoveEnchant+".enchantments").getKeys(false).contains(enchantToDelete))){
                                    kits.set(path+".items."+itemToRemoveEnchant+".enchantments."+enchantToDelete, null);
                                    plugin.getKits().saveConfig();

                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &aEnchant &l"+enchantToDelete+" &aremoved from item &l"+itemToRemoveEnchant+"&a!"));
                                    player.playSound(player.getLocation(), good, 1f, 1.4f);
                                }
                                else{
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cEnchant &l"+enchantToDelete+" &cdoes not exist for the item &l"+itemToRemoveEnchant));
                                    player.playSound(player.getLocation(), invalid, 1f, 1f);
                                    return true;
                                }
                            }
                            else{
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cItem &l"+itemToRemoveEnchant+" &cdoes not exist in kit &l"+kitName+"&c!"));
                                player.playSound(player.getLocation(), invalid, 1f, 1f);
                                return true;
                            }
                            break;

                        default:
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cUnkown command. Use &l/kitconfighelp &cfor info."));
                            player.playSound(player.getLocation(), invalid, 1f, 1f);
                            break;
                    }
                    break;

                default:
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix + " &cUnkown command. Use &l/kitconfighelp &cfor info."));
                    player.playSound(player.getLocation(), invalid, 1f, 1f);
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
