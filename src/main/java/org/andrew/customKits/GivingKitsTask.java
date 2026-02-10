//Developed by: _ItsAndrew_
package org.andrew.customKits;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GivingKitsTask {
    private final CustomKits plugin;

    public GivingKitsTask(CustomKits plugin){
        this.plugin = plugin;
    }

    public void giveKit(Player player, String displayName){
        FileConfiguration kits = plugin.getKits().getConfig();
        FileConfiguration playerData = plugin.getPlayerData().getConfig();
        boolean isCooldownForeverOption = plugin.getConfig().getBoolean("kits-cooldown-forever");
        boolean gotKitAlready = playerData.getBoolean("players."+player.getName()+".gotKitAlready");

        //Checks whether the player has cooldown
        if(!isCooldownForeverOption){
            if(plugin.getCooldownManager().hasCooldown(player)){
                long remainingCooldown = plugin.getCooldownManager().getRemainingCooldown(player);
                String soundName = plugin.getConfig().getString("cooldown-sound");
                String remainingTime = formatTime(remainingCooldown);

                NamespacedKey trueSound = NamespacedKey.minecraft(soundName.toLowerCase());
                Sound noPermissionSound = Registry.SOUNDS.get(trueSound);
                player.playSound(player.getLocation(), noPermissionSound, 1f, 1f);

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou still have a cooldown of &l"+remainingTime+"&c!"));
                player.closeInventory();
                return;
            }
        }

        //Checks if kits-cooldown-forever is true and if the player already got a kit
        if(isCooldownForeverOption){
            if(gotKitAlready){
                String soundName = plugin.getConfig().getString("got-kit-already");
                NamespacedKey soundInGame = NamespacedKey.minecraft(soundName.toLowerCase());
                Sound playerGotKitAlready = Registry.SOUNDS.get(soundInGame);
                player.playSound(player.getLocation(), playerGotKitAlready, 1f, 1f);

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("player-already-got-kit")));
                player.closeInventory();
                return;
            }
        }

        //Looping through the kits and giving the kit that matches the display name
        for(String kit : kits.getConfigurationSection("kits").getKeys(false)){
            String kitDisplayName = plugin.getConfig().getString("kits."+kit+".title");
            if(kitDisplayName.equalsIgnoreCase(displayName)){
                String permission = kits.getString("kits."+kit+".permission");

                //Check for permission
                if(plugin.getPermissions().playerHas(player, permission)){
                    for(String item : kits.getConfigurationSection("kits."+kit+".items").getKeys(false)){
                        int itemQuantity = kits.getInt("kits."+kit+".items."+item+".quantity");
                        String itemMaterial = kits.getString("kits."+kit+".items."+item+".material");
                        Material material = Material.matchMaterial(itemMaterial.toUpperCase());
                        ItemStack itemS = new ItemStack(material, itemQuantity);
                        ItemMeta itemMeta = itemS.getItemMeta();

                        //Setting the enchantments (if there are any)
                        ConfigurationSection enchSection = kits.getConfigurationSection("kits."+kit+".items."+item+".enchantments");
                        if(enchSection != null){
                            for(String enchant : enchSection.getKeys(false)){
                                int enchantLevel = kits.getInt("kits."+kit+".items."+item+".enchantments."+enchant);
                                Enchantment realEnchant = Enchantment.getByName(enchant);

                                if(realEnchant != null){
                                    itemMeta.addEnchant(realEnchant, enchantLevel, true);
                                }
                            }
                        }

                        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Kit &l"+kits.getString("kits."+kit+".title")));
                        itemS.setItemMeta(itemMeta);

                        //Check if player has inventory space
                        if(player.getInventory().firstEmpty() == -1){
                            String soundName = plugin.getConfig().getString("no-inventory-space-sound");
                            NamespacedKey sound = NamespacedKey.minecraft(soundName.toLowerCase());
                            Sound noInvSpaceSound = Registry.SOUNDS.get(sound);
                            player.playSound(player.getLocation(), noInvSpaceSound, 1f, 1f);

                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have enough space in your inventory!"));
                            return;
                        }
                        player.getInventory().addItem(itemS);
                    }

                    plugin.getCooldownManager().startCooldown(player);
                    Sound getKitSound = Registry.SOUNDS.get(NamespacedKey.minecraft(plugin.getConfig().getString("player-get-kit-sound").toLowerCase()));
                    float gksVolume = plugin.getConfig().getInt("pgks-volume");
                    float gksPitch = plugin.getConfig().getInt("pgks-pitch");

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aKit "+kits.getString("kits."+kit+".title")+" &aacquired successfully!"));
                    player.playSound(player.getLocation(), getKitSound, gksVolume, gksPitch);
                    player.closeInventory();
                }
                else{
                    String soundName = plugin.getConfig().getString("no-permission-sound");
                    NamespacedKey trueSound = NamespacedKey.minecraft(soundName.toLowerCase());
                    Sound cooldownSound = Registry.SOUNDS.get(trueSound);
                    player.playSound(player.getLocation(), cooldownSound, 1f, 1f);

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to get kit "+kits.getString("kits."+kit+".title")+"&c!"));
                    player.closeInventory();
                    return;
                }
            }
        }
    }

    //Displays the remaining cooldown (if it is toggled)
    private String formatTime(long seconds){
        long days = seconds / 86000;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) /60;
        long secs = seconds % 60;

        StringBuilder sb = new StringBuilder();
        if(days > 0) sb.append(days).append("d ");
        if(hours > 0 || days > 0) sb.append(hours).append("h ");
        if(minutes > 0 || hours > 0 || days > 0) sb.append(minutes).append("m ");
        sb.append(secs).append("s ");

        return sb.toString().trim();
    }
}
