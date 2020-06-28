package com.orf9.antixrayplus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;


public final class Main extends JavaPlugin implements Listener {
    private File playerDataFile;
    private YamlConfiguration modifyPlayerData;

    public YamlConfiguration getPlayerData(){ return modifyPlayerData; }
    public File getPlayerDataFile(){ return playerDataFile; }

    @Override
    public void onEnable() {


        Bukkit.getPluginManager().registerEvents(this, this);

        this.getConfig().options().copyDefaults();
        saveDefaultConfig();

        getCommand("antixrayplus").setExecutor(new commandAntiXrayPlus());

        try {
            initiateFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int pluginId = 8008; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {
    }

    public void initiateFiles() throws IOException {
        playerDataFile = new File(this.getDataFolder(), "playerdata.yml");

        if (!playerDataFile.exists()){
            playerDataFile.createNewFile();
        }
        modifyPlayerData = YamlConfiguration.loadConfiguration(playerDataFile);

    }



    @EventHandler
    public void onMine(BlockBreakEvent e){
        Player player = e.getPlayer();
        String uuid = player.getUniqueId().toString();
        Block block = e.getBlock();

        int oldStoneMined = (int) getPlayerData().get(uuid + ".stoneMined");
        int oldDiamondOreMined = (int) getPlayerData().get(uuid + ".diamondOreMined");

        int stoneMined = oldStoneMined;
        int diamondOreMined = oldDiamondOreMined;

        double ratio;

        if (block.getType().equals(Material.DIAMOND_ORE)){
            ItemStack item = player.getInventory().getItemInMainHand();
            if (!item.containsEnchantment(Enchantment.SILK_TOUCH)){
                diamondOreMined++;
                getPlayerData().set(uuid + ".diamondOreMined", diamondOreMined);
                try {
                    getPlayerData().save(getPlayerDataFile());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }else if (block.getType().equals(Material.STONE)) {
            stoneMined++;
            getPlayerData().set(uuid + ".stoneMined", stoneMined);
            try {
                getPlayerData().save(getPlayerDataFile());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        if (stoneMined != oldStoneMined || diamondOreMined != oldDiamondOreMined){
            if (!(diamondOreMined == 0) && !(stoneMined == 0)){
                ratio = (double) diamondOreMined / (double) stoneMined;
                getPlayerData().set(uuid + ".ratio", ratio);

                Double maxRatio = getConfig().getDouble("maxRatio");

                int minStone = getConfig().getInt("stoneMinimum");
                int minDiamondOre = getConfig().getInt("diamondMinimum");
                if (minStone == 0){
                    minStone = 1024;
                }
                if (minDiamondOre == 0){
                    minDiamondOre = 32;
                }
                if (maxRatio == 0){
                    maxRatio = 0.002;
                }

                if (stoneMined > minStone ) {


                    if (diamondOreMined > minDiamondOre) {

                        if (ratio > maxRatio) {

                            getLogger().log(Level.WARNING, "Player " + player.getName() + " may be using cheats! Ratio: " + ratio);

                            Bukkit.getOnlinePlayers().forEach(pl -> {
                                if (pl.hasPermission("antixrayplus.alerts")) {
                                    pl.sendMessage(ChatColor.RED + "Player " + player + " may be using XRAY! Ratio:" + ratio);
                                }
                            });

                        }
                    }
                }
            }

            getPlayerData().set(uuid + ".diamondOreMined", diamondOreMined);
            getPlayerData().set(uuid + ".stoneMined", stoneMined);


            try {
                getPlayerData().save(getPlayerDataFile());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        String uuid = player.getUniqueId().toString();

        if (!getPlayerData().contains(uuid)){
            getPlayerData().createSection(uuid);
            getPlayerData().createSection(uuid + ".playerName");
            getPlayerData().createSection(uuid + ".diamondOreMined");
            getPlayerData().createSection(uuid + ".stoneMined");
            getPlayerData().createSection(uuid + ".ratio");

            getPlayerData().set(uuid + ".playerName", player.getName());
            getPlayerData().set(uuid + ".diamondOreMined", 0);
            getPlayerData().set(uuid + ".stoneMined", 0);
            getPlayerData().set(uuid + ".ratio", 0);

            try {
                getPlayerData().save(getPlayerDataFile());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
