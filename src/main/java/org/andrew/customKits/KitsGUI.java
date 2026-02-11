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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class KitsGUI implements Listener {
    private final CustomKits plugin;

    public KitsGUI(CustomKits plugin){
        this.plugin = plugin;
    }

    //Shows the GUI to the player
    public void showGUI(Player player){
        FileConfiguration kits = plugin.getKits().getConfig();

        //Check if there are any configured kits
        ConfigurationSection kitsSection = kits.getConfigurationSection("kits");
        if(kitsSection == null || kitsSection.getKeys(false).isEmpty()){
            String noKitsMessage = plugin.getConfig().getString("no-kits-configured");
            Sound noKitsSound = Registry.SOUNDS.get(NamespacedKey.minecraft(plugin.getConfig().getString("no-kits-configured-sound").toLowerCase()));
            float nksVolume = plugin.getConfig().getInt("nkcs-volume");
            float nksPitch = plugin.getConfig().getInt("nkcs-pitch");

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', noKitsMessage));
            player.playSound(player.getLocation(), noKitsSound, nksVolume, nksPitch);
            return;
        }

        String invTitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("gui-title")));
        Inventory kitsGUI = Bukkit.createInventory(null, plugin.getInvSize(), invTitle);

        //Displays decorations (if they are toggled)
        boolean toggleDecorations = plugin.getConfig().getBoolean("toggle-decorations");
        boolean toggleInfoItem = plugin.getConfig().getBoolean("info-item.toggle");
        if(toggleDecorations){
            String itemString = plugin.getConfig().getString("decoration-item.material");
            ItemStack decoItem = new ItemStack(Material.matchMaterial(itemString.toUpperCase())); //Gets the actual item
            ItemMeta diMeta = decoItem.getItemMeta(); //Gets the meta
            String diDisplayName = plugin.getConfig().getString("decoration-item.display-name"); //Gets the display name from config

            for(int i = 0; i<=8; i++){
                if(toggleInfoItem){ //Skips the slot 4 if info item is toggled
                    if(i == 4) continue;
                }

                diMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', diDisplayName));
                decoItem.setItemMeta(diMeta);
                kitsGUI.setItem(i, decoItem);
            }
            for(int i = 45; i<=53; i++){
                diMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', diDisplayName));
                decoItem.setItemMeta(diMeta);
                kitsGUI.setItem(i, decoItem);
            }
        }

        //Displays the info item if it is toggled
        if(toggleInfoItem){
            String infoItemString = plugin.getConfig().getString("info-item.material");
            String iiDisplayName = plugin.getConfig().getString("info-item.display-name");
            ItemStack infoItem = new ItemStack(Material.matchMaterial(infoItemString.toUpperCase())); //Gets the actual item
            ItemMeta iiMeta = infoItem.getItemMeta(); //Gets the meta of the item

            //Assigns the display name and the lore of the item
            iiMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', iiDisplayName));
            List<String> coloredLore = new ArrayList<>();
            if(plugin.getConfig().getStringList("info-item.lore").isEmpty()){ //Sets no lore if the lore of the item is empty
                coloredLore = Collections.emptyList();
                iiMeta.setLore(coloredLore);
            }
            else{
                for(String rawLine : plugin.getConfig().getStringList("info-item.lore")){
                    String coloredLine = ChatColor.translateAlternateColorCodes('&', rawLine);
                    coloredLore.add(coloredLine);
                }
                iiMeta.setLore(coloredLore);
            }

            infoItem.setItemMeta(iiMeta);
            kitsGUI.setItem(4, infoItem);
        }

        //Displays the exit item (if it is toggled)
        boolean toggleExitItem = plugin.getConfig().getBoolean("exit-item.toggle");
        if(toggleExitItem){
            int exitItemSlot = plugin.getConfig().getInt("exit-item.slot");
            Material exitItemMaterial = Material.matchMaterial(plugin.getConfig().getString("exit-item.material").toUpperCase());
            ItemStack exitItem = new ItemStack(exitItemMaterial);
            ItemMeta exitItemMeta = exitItem.getItemMeta();

            exitItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("exit-item.display-name"))));
            exitItem.setItemMeta(exitItemMeta);
            kitsGUI.setItem(exitItemSlot, exitItem);
        }

        try{
            for(String key : kits.getConfigurationSection("kits").getKeys(false)){
                String kitPath = "kits."+key;
                String kitItem = kits.getString(kitPath + ".gui-item");
                String kitTitle = ChatColor.translateAlternateColorCodes('&', kits.getString(kitPath+".title"));
                String permission = kits.getString(kitPath+".permission");
                int kitsSlot = kits.getInt(kitPath+".gui-slot");
                boolean isGlowEnchant = kits.getBoolean(kitPath+".enchantglow", false);

                Material kitMaterial = Material.matchMaterial(kitItem.toUpperCase());
                ItemStack item = new ItemStack(kitMaterial);
                ItemMeta kitMeta = item.getItemMeta();

                //Displaying the kits in the GUI
                //Setting the display name
                kitMeta.setDisplayName(kitTitle);

                //Setting the enchant glint if it is toggled for the kit
                if(isGlowEnchant){
                    kitMeta.addEnchant(Enchantment.LURE, 1, true);
                    kitMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }

                //Setting the lore
                List<String> coloredLore = new ArrayList<>();
                for(String loreLine : kits.getStringList(kitPath + ".lore")) coloredLore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
                if(!plugin.getPermissions().playerHas(player, permission)){
                    String noPermLore = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("no-permission-lore"));
                    coloredLore.add(" ");
                    coloredLore.add(noPermLore);
                }
                kitMeta.setLore(coloredLore);

                item.setItemMeta(kitMeta);
                kitsGUI.setItem(kitsSlot, item);
            }
        } catch (Exception e){
            String errorMessage = plugin.getConfig().getString("error-message");
            Sound errorSound = Registry.SOUNDS.get(NamespacedKey.minecraft(plugin.getConfig().getString("kits-command-error-sound").toLowerCase()));
            float errorSoundVolume = plugin.getConfig().getInt("kces-volume");
            float errorSoundPitch = plugin.getConfig().getInt("kces-pitch");

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', errorMessage));
            player.playSound(player.getLocation(), errorSound, errorSoundVolume, errorSoundPitch);
            Bukkit.getLogger().warning("[CUSTOMKITS] " + e.getMessage());
            return;
        }
        String soundName = plugin.getConfig().getString("kits-sound");
        NamespacedKey trueSound = NamespacedKey.minecraft(soundName.toLowerCase());
        Sound sound = Registry.SOUNDS.get(trueSound);
        float ksVolume = plugin.getConfig().getInt("ks-volume");
        float ksPitch = plugin.getConfig().getInt("ks-pitch");

        player.playSound(player.getLocation(), sound, ksVolume, ksPitch);
        player.openInventory(kitsGUI);
    }

    //When a player click's on an item in the GUI
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String guiTtile = plugin.getConfig().getString("gui-title");
        if (!(event.getView().getTitle().equals(guiTtile))) return;

        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().equals(Material.AIR)) return;

        ItemMeta meta = clicked.getItemMeta();
        if (meta == null) return;

        //If the player clicks on the exit item
        Material exitButtonMaterial = Material.matchMaterial(plugin.getConfig().getString("exit-item.material").toUpperCase());
        if (clicked.getType().equals(exitButtonMaterial)){
            Sound exitItemSound = Registry.SOUNDS.get(NamespacedKey.minecraft(plugin.getConfig().getString("exit-item-sound").toLowerCase()));
            float eisVolume = plugin.getConfig().getInt("eis-volume");
            float eisPitch = plugin.getConfig().getInt("eis-pitch");

            player.playSound(player.getLocation(), exitItemSound, eisVolume, eisPitch);
            player.closeInventory();
            return;
        }

        //If the player clicks on info item or decoration item
        Material infoItemMat = Material.matchMaterial(plugin.getConfig().getString("info-item.material").toUpperCase());
        Material decoItemMat = Material.matchMaterial(plugin.getConfig().getString("decoration-item.material").toUpperCase());
        if(clicked.getType().equals(infoItemMat) || clicked.getType().equals(decoItemMat)) return;

        player.sendMessage(meta.getDisplayName());
        plugin.getGiveKitsTask().giveKit(player, meta.getDisplayName()); //Gives the kit to the player
    }
}
