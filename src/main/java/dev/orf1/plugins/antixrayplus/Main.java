package dev.orf1.plugins.antixrayplus;

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
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;


public final class Main extends JavaPlugin implements Listener {

    HashMap<String, Integer> stoneMined = new HashMap<>();
    HashMap<String, Integer> diamondOreMined = new HashMap<>();
    HashMap<String, Double> ratio = new HashMap<>();
    Double maxRatio = getConfig().getDouble("max-ratio");
    int minStone = this.getConfig().getInt("minimum-stone");
    int minDiamondOre = this.getConfig().getInt("minimum-diamond");
    int updateFrequency = this.getConfig().getInt("update-frequency");
    private File playerDataFile;
    private YamlConfiguration modifyPlayerData;
    String configVersion = this.getConfig().getString("config-version");

    @Override
    public void onEnable() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.kickPlayer("Server reloaded, please re-join!");
        }
        Bukkit.getPluginManager().registerEvents(this, this);

        this.getConfig().options().copyDefaults();
        saveDefaultConfig();

        getCommand("antixrayplus").setExecutor(new CommandAntiXrayPlus());

        try {
            initiateFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int pluginId = 8008;
        Metrics metrics = new Metrics(this, pluginId);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String uuid = player.getUniqueId().toString();

                getPlayerData().set(uuid + ".stoneMined", this.stoneMined.get(uuid));
                getPlayerData().set(uuid + ".diamondOreMined", this.diamondOreMined.get(uuid));
                getPlayerData().set(uuid + ".ratio", this.ratio.get(uuid));

                try {
                    getPlayerData().save(getPlayerDataFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, getConfig().getInt("update-frequency"));
    }


    @Override
    public void onDisable() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            String uuid = onlinePlayer.getUniqueId().toString();
            getPlayerData().set(uuid + ".stoneMined", this.stoneMined.get(uuid));
            getPlayerData().set(uuid + ".diamondOreMined", this.diamondOreMined.get(uuid));
            getPlayerData().set(uuid + ".ratio", this.ratio.get(uuid));

            try {
                getPlayerData().save(getPlayerDataFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void initiateFiles() throws IOException {
        playerDataFile = new File(this.getDataFolder(), "playerdata.yml");

        if (!playerDataFile.exists()) {
            playerDataFile.createNewFile();
        }
        modifyPlayerData = YamlConfiguration.loadConfiguration(playerDataFile);

    }


    @EventHandler
    public void onMine(BlockBreakEvent e) {
        Player player = e.getPlayer();
        String uuid = player.getUniqueId().toString();
        Block block = e.getBlock();
        if (block.getType().equals(Material.DIAMOND_ORE) || block.getType().equals(Material.STONE)) {

            int oldStoneMined = this.stoneMined.get(uuid);
            int oldDiamondOreMined = this.diamondOreMined.get(uuid);

            int stoneMined = oldStoneMined;
            int diamondOreMined = oldDiamondOreMined;

            double ratio;
            boolean wasDiamond = false;

            if (block.getType().equals(Material.DIAMOND_ORE)) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (!item.containsEnchantment(Enchantment.SILK_TOUCH)) {
                    diamondOreMined++;
                    this.diamondOreMined.put(uuid, diamondOreMined);
                    wasDiamond = true;
                }
            } else if (block.getType().equals(Material.STONE)) {
                stoneMined++;
                this.stoneMined.put(uuid, stoneMined);
            }
            if (stoneMined != oldStoneMined || diamondOreMined != oldDiamondOreMined) {
                if (!(diamondOreMined == 0) && !(stoneMined == 0)) {
                    ratio = (double) diamondOreMined / (double) stoneMined;
                    this.ratio.put(uuid, ratio);

                    Double maxRatio = this.maxRatio;

                    int minStone = this.minStone;
                    int minDiamondOre = this.minDiamondOre;

                    if (stoneMined > minStone) {

                        if (diamondOreMined > minDiamondOre) {

                            if (ratio > maxRatio && wasDiamond) {

                                getLogger().log(Level.WARNING, "Player " + player.getName() + " may be using cheats! Ratio: " + ratio);

                                Bukkit.getOnlinePlayers().forEach(pl -> {
                                    if (pl.hasPermission("antixrayplus.alerts")) {
                                        pl.sendMessage(ChatColor.RED + "Player " + player.getName() + " may be using XRAY! Ratio:" + ratio);
                                    }
                                });

                            }
                        }
                    }
                }
                this.stoneMined.put(uuid, stoneMined);
                this.diamondOreMined.put(uuid, diamondOreMined);

            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String uuid = player.getUniqueId().toString();


        if (!getPlayerData().contains(uuid) || !getPlayerData().contains(uuid + ".diamondOreMined") || !getPlayerData().contains(uuid + ".stoneMined") || !getPlayerData().contains(uuid + ".ratio")) {
            getPlayerData().createSection(uuid);
            getPlayerData().createSection(uuid + ".playerName");
            getPlayerData().createSection(uuid + ".diamondOreMined");
            getPlayerData().createSection(uuid + ".stoneMined");
            getPlayerData().createSection(uuid + ".ratio");

            getPlayerData().set(uuid + ".playerName", player.getName());
            getPlayerData().set(uuid + ".diamondOreMined", 0);
            getPlayerData().set(uuid + ".stoneMined", 0);
            getPlayerData().set(uuid + ".ratio", 0.0);

            try {
                getPlayerData().save(getPlayerDataFile());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            this.stoneMined.put(uuid, 0);
            this.diamondOreMined.put(uuid, 0);
            this.ratio.put(uuid, 0.0);
        } else {
            this.stoneMined.put(uuid, (int) getPlayerData().get(uuid + ".stoneMined"));
            this.diamondOreMined.put(uuid, (int) getPlayerData().get(uuid + ".diamondOreMined"));
            this.ratio.put(uuid, (double) getPlayerData().get(uuid + ".ratio"));
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();

        getPlayerData().set(uuid + ".stoneMined", this.stoneMined.get(uuid));
        getPlayerData().set(uuid + ".diamondOreMined", this.diamondOreMined.get(uuid));
        getPlayerData().set(uuid + ".ratio", this.ratio.get(uuid));

        try {
            getPlayerData().save(getPlayerDataFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();

        getPlayerData().set(uuid + ".stoneMined", this.stoneMined.get(uuid));
        getPlayerData().set(uuid + ".diamondOreMined", this.diamondOreMined.get(uuid));
        getPlayerData().set(uuid + ".ratio", this.ratio.get(uuid));

        try {
            getPlayerData().save(getPlayerDataFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public YamlConfiguration getPlayerData() {
        return modifyPlayerData;
    }

    public File getPlayerDataFile() {
        return playerDataFile;
    }
    
}