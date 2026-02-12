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

    public GivingKitsTask(CustomKits plugin) {
        this.plugin = plugin;
    }

    public void giveKit(Player player, String kitID) {
        FileConfiguration kits = plugin.getKits().getConfig();
        FileConfiguration playerData = plugin.getPlayerData().getConfig();
        boolean isCooldownForeverOption = plugin.getConfig().getBoolean("kits-cooldown-forever");
        boolean gotKitAlready = playerData.getBoolean("players." + player.getName() + ".gotKitAlready");

        //Checks whether the player has cooldown
        if (!isCooldownForeverOption && plugin.getCooldownManager().hasCooldown(player)) {
            long remainingCooldown = plugin.getCooldownManager().getRemainingCooldown(player);
            String soundName = plugin.getConfig().getString("cooldown-sound");
            String remainingTime = formatTime(remainingCooldown);

            NamespacedKey trueSound = NamespacedKey.minecraft(soundName.toLowerCase());
            Sound noPermissionSound = Registry.SOUNDS.get(trueSound);
            player.playSound(player.getLocation(), noPermissionSound, 1f, 1f);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou still have a cooldown of &l" + remainingTime + "&c!"));
            player.closeInventory();
            return;
        }

        //Checks if kits-cooldown-forever is true and if the player already got a kit
        if (isCooldownForeverOption && gotKitAlready) {
            String soundName = plugin.getConfig().getString("got-kit-already");
            NamespacedKey soundInGame = NamespacedKey.minecraft(soundName.toLowerCase());
            Sound playerGotKitAlready = Registry.SOUNDS.get(soundInGame);
            player.playSound(player.getLocation(), playerGotKitAlready, 1f, 1f);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("player-already-got-kit")));
            player.closeInventory();
            return;
        }

        String permission = kits.getString("kits." + kitID + ".permission");

        //Checking if the player has permission to get the kit
        if (!plugin.getPermissions().playerHas(player, permission)) {
            String soundName = plugin.getConfig().getString("no-permission-sound");
            NamespacedKey trueSound = NamespacedKey.minecraft(soundName.toLowerCase());
            Sound cooldownSound = Registry.SOUNDS.get(trueSound);
            player.playSound(player.getLocation(), cooldownSound, 1f, 1f);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to get kit " + kits.getString("kits." + kitID + ".title") + "&c!"));
            player.closeInventory();
            return;
        }

        //Checking if the kit has any items to it
        ConfigurationSection kitItems = kits.getConfigurationSection("kits." + kitID + ".items");
        if (kitItems == null) {
            String soundName = plugin.getConfig().getString("kit-has-no-items-sound");
            float khnisVolume = plugin.getConfig().getInt("khnis-volume");
            float khnisPitch = plugin.getConfig().getInt("khnis-pitch");
            player.playSound(player.getLocation(), Registry.SOUNDS.get(NamespacedKey.minecraft(soundName)), khnisVolume, khnisPitch);

            player.closeInventory();
            Bukkit.getLogger().warning("[CUSTOMKITS] The kit " + kitID + " does not have any items!");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis kit doesn't have any items assigned! Contact the server administrators about this."));
            return;
        }

        //Check if player has inventory space
        if (player.getInventory().firstEmpty() == -1) {
            String soundName = plugin.getConfig().getString("no-inventory-space-sound");
            NamespacedKey sound = NamespacedKey.minecraft(soundName.toLowerCase());
            Sound noInvSpaceSound = Registry.SOUNDS.get(sound);
            player.playSound(player.getLocation(), noInvSpaceSound, 1f, 1f);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have enough space in your inventory!"));
            return;
        }

        for (String item : kits.getConfigurationSection("kits." + kitID + ".items").getKeys(false)) {
            int itemQuantity = kits.getInt("kits." + kitID + ".items." + item + ".quantity");
            String itemMaterial = kits.getString("kits." + kitID + ".items." + item + ".material");
            Material material = Material.matchMaterial(itemMaterial.toUpperCase());
            ItemStack itemS = new ItemStack(material, itemQuantity);
            ItemMeta itemMeta = itemS.getItemMeta();

            //Setting the enchantments (if there are any)
            ConfigurationSection enchSection = kits.getConfigurationSection("kits." + kitID + ".items." + item + ".enchantments");
            if (enchSection != null) {
                for (String enchant : enchSection.getKeys(false)) {
                    int enchantLevel = kits.getInt("kits." + kitID + ".items." + item + ".enchantments." + enchant);
                    Enchantment realEnchant = Enchantment.getByName(enchant);

                    if (realEnchant != null) {
                        itemMeta.addEnchant(realEnchant, enchantLevel, true);
                    }
                }
            }

            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Kit &l" + kits.getString("kits." + kitID + ".title")));
            itemS.setItemMeta(itemMeta);

            //Adding the item to the player's inventory
            player.getInventory().addItem(itemS);
        }

        plugin.getCooldownManager().startCooldown(player);
        Sound getKitSound = Registry.SOUNDS.get(NamespacedKey.minecraft(plugin.getConfig().getString("player-get-kit-sound").toLowerCase()));
        float gksVolume = plugin.getConfig().getInt("pgks-volume");
        float gksPitch = plugin.getConfig().getInt("pgks-pitch");

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aKit " + kits.getString("kits." + kitID + ".title") + " &aacquired successfully!"));
        player.playSound(player.getLocation(), getKitSound, gksVolume, gksPitch);
        player.closeInventory();
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