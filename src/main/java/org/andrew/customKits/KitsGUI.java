//Developed by: _ItsAndrew_
package org.andrew.customKits;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KitsGUI implements Listener {
    CustomKits plugin;

    public KitsGUI(CustomKits plugin){
        this.plugin = plugin;
    }

    //Shows the GUI to the player
    public void showGUI(Player player){
        FileConfiguration kits = plugin.getKits().getConfig();
        FileConfiguration messages = plugin.getMessages().getConfig();
        String prefix = messages.getString("prefix");

        if(!kits.isConfigurationSection("kits")){
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("prefix")+" &cThere are no kits configured yet! Please contact the server administrators."));
            return;
        }

        String invTitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("gui-title")));
        Inventory inv = Bukkit.createInventory(null, plugin.getInvSize(), invTitle);

        try{
            String soundName = plugin.getConfig().getString("kits-sound");

            NamespacedKey trueSound = NamespacedKey.minecraft(soundName.toLowerCase());
            Sound sound = Registry.SOUNDS.get(trueSound);

            player.playSound(player.getLocation(), sound, 1f, 1f);
            for(String key : kits.getConfigurationSection("kits").getKeys(false)){
                String kitPath = "kits."+key;
                String kitItem = kits.getString(kitPath + ".gui-item");
                String kitTitle = ChatColor.translateAlternateColorCodes('&', kits.getString(kitPath+".title"));
                String permission = kits.getString(kitPath+".permission");
                int kitsSlot = kits.getInt(kitPath+".gui-slot");
                boolean isGlowEnchant = kits.getBoolean(kitPath+".enchantglow");

                Material kitMaterial = Material.matchMaterial(kitItem.toUpperCase());
                ItemStack item = new ItemStack(kitMaterial);
                ItemMeta kitMeta = item.getItemMeta();

                if(player.hasPermission(permission)){
                    kitMeta.setDisplayName(kitTitle);
                    if(isGlowEnchant){
                        kitMeta.addEnchant(Enchantment.LURE, 1, true);
                        kitMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }

                    List<String> coloredLore = new ArrayList<>();
                    for(String loreLine : kits.getStringList(kitPath + ".lore")){
                        coloredLore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
                    }
                    kitMeta.setLore(coloredLore);
                }
                else{
                    kitMeta.setDisplayName(kitTitle);

                    List<String> coloredLore = new ArrayList<>();
                    for(String loreLine : kits.getStringList(kitPath + ".lore")){
                        coloredLore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
                    }
                    coloredLore.add("");
                    coloredLore.add(ChatColor.translateAlternateColorCodes('&', "&cNOPERMISSION!"));
                    kitMeta.setLore(coloredLore);
                }

                item.setItemMeta(kitMeta);
                inv.setItem(kitsSlot,item);
            }
        } catch (Exception e){
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cError! Contact the server administrators about this."));
            Bukkit.getLogger().warning("[CK] " + e.getMessage());
            return;
        }

        if(plugin.getConfig().getBoolean("exit-item.toggle")){
            int exitItemSlot = plugin.getConfig().getInt("exit-item.slot");
            Material exitItemMaterial = Material.matchMaterial(plugin.getConfig().getString("exit-item.material").toUpperCase());
            ItemStack exitItem = new ItemStack(exitItemMaterial);
            ItemMeta exitItemMeta = exitItem.getItemMeta();

            exitItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("exit-item.display-name"))));
            exitItem.setItemMeta(exitItemMeta);
            inv.setItem(exitItemSlot, exitItem);
        }
        player.openInventory(inv);
    }

    //When a player click's on an item in the GUI
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player player)) return;

        String guiTtile = plugin.getConfig().getString("gui-title");
        if(!(event.getView().getTitle().equals(guiTtile))) return;

        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if(clicked == null || clicked.getType() == Material.AIR) return;

        ItemMeta meta = clicked.getItemMeta();
        if(meta == null) return;

        Material exitButtonMaterial = Material.matchMaterial(plugin.getConfig().getString("exit-item.material").toUpperCase());
        if(clicked.getType() == exitButtonMaterial){
            player.closeInventory();
            return;
        }

        FileConfiguration kits = plugin.getKits().getConfig();
        FileConfiguration playerData = plugin.getPlayerData().getConfig();
        String prefix = plugin.getMessages().getConfig().getString("prefix");
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

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cYou still have a cooldown of &l"+remainingTime+"&c!"));
                player.closeInventory();
                return;
            }
        }

        //Check whether the player has permission
        Material clickedMaterial = clicked.getType();
        for(String kit : kits.getConfigurationSection("kits").getKeys(false)){
            Material kitMaterial = Material.valueOf(kits.getString("kits."+kit+".gui-item").toUpperCase());
            if(clickedMaterial == kitMaterial){
                String permission = kits.getString("kits."+kit+".permission");

                //Checks if kits-cooldown-forever is true and if the player already got a kit
                if(isCooldownForeverOption){
                    if(gotKitAlready){
                        String soundName = plugin.getConfig().getString("got-kit-already");
                        NamespacedKey soundInGame = NamespacedKey.minecraft(soundName.toLowerCase());
                        Sound playerGotKitAlready = Registry.SOUNDS.get(soundInGame);
                        player.playSound(player.getLocation(), playerGotKitAlready, 1f, 1f);

                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+plugin.getConfig().getString("player-already-got-kit")));
                        player.closeInventory();
                        return;
                    }
                }

                //Check for permission
                if(player.hasPermission(permission)){
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

                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cYou don't have enough space in your inventory!"));
                            return;
                        }
                        player.getInventory().addItem(itemS);
                    }

                    plugin.getCooldownManager().startCooldown(player);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &aKit "+kits.getString("kits."+kit+".title")+" &aacquired successfully!"));
                    player.closeInventory();
                }
                else{
                    String soundName = plugin.getConfig().getString("no-permission-sound");
                    NamespacedKey trueSound = NamespacedKey.minecraft(soundName.toLowerCase());
                    Sound cooldownSound = Registry.SOUNDS.get(trueSound);
                    player.playSound(player.getLocation(), cooldownSound, 1f, 1f);

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " &cYou don't have permission to get kit "+kits.getString("kits."+kit+".title")+"&c!"));
                    player.closeInventory();
                    return;
                }
            }
        }
    }

    //Formats the cooldown time (from seconds to ...)
    public String formatTime(long seconds){
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
