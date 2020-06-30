package com.orf9.antixrayplus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class CommandAntiXrayPlus implements CommandExecutor {
    private final Main main = JavaPlugin.getPlugin(Main.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {

            Player player = (Player) sender;
            if (player.hasPermission("antixrayplus.use")) {
                if (args.length == 1) {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target != null) {
                        String uuid = target.getUniqueId().toString();

                        int stoneMined = main.stoneMined.get(uuid);
                        int diamondOreMined = main.diamondOreMined.get(uuid);

                        if (!(diamondOreMined == 0) && !(stoneMined == 0)) {
                            double ratio = main.ratio.get(uuid);

                            player.sendMessage("Blocks mined by: " + ChatColor.GREEN + target.getName());
                            player.sendMessage("Diamond Ore: " + diamondOreMined);
                            player.sendMessage("Stone: " + stoneMined);
                            player.sendMessage("Ratio of Diamond to Stone: " + ratio);

                            Double maxRatio = main.maxRatio;

                            int minStone = main.minStone;
                            int minDiamondOre = main.minDiamondOre;

                            if (ratio > maxRatio && stoneMined > minStone && diamondOreMined > minDiamondOre) {
                                player.sendMessage(ChatColor.RED + "Player as been detected as suspicious!");
                            }
                        } else {
                            player.sendMessage("Blocks mined by: " + ChatColor.GREEN + target.getName());
                            player.sendMessage("Diamond Ore: " + diamondOreMined);
                            player.sendMessage("Stone: " + stoneMined);
                            player.sendMessage(ChatColor.RED + "This player has not mined enough blocks yet to display ratio.");
                        }


                    } else if (args[0].equalsIgnoreCase("reload")) {
                        main.reloadConfig();
                        main.maxRatio = main.getConfig().getDouble("max-ratio");
                        main.minStone = main.getConfig().getInt("minimum-stone");
                        main.minDiamondOre = main.getConfig().getInt("minimum-diamond");
                        main.updateFrequency = main.getConfig().getInt("update-frequency");
                        player.sendMessage("Config Reloaded!");
                    } else {
                        player.sendMessage("Invalid Arguments! Usage: /antixrayplus [player] | /antixrayplus reload");
                    }

                } else {
                    player.sendMessage("Invalid Arguments! Usage: /antixrayplus [player] | /antixrayplus reload");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
        } else {

            if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    String uuid = target.getUniqueId().toString();

                    int stoneMined = main.stoneMined.get(uuid);
                    int diamondOreMined = main.diamondOreMined.get(uuid);

                    if (!(diamondOreMined == 0) && !(stoneMined == 0)) {
                        double ratio = main.ratio.get(uuid);

                        sender.sendMessage("Blocks mined by: " + target.getName());
                        sender.sendMessage("Diamond Ore: " + diamondOreMined);
                        sender.sendMessage("Stone: " + stoneMined);
                        sender.sendMessage("Ratio of Diamond to Stone: " + ratio);

                        double maxRatio = main.maxRatio;

                        int minStone = main.minStone;
                        int minDiamondOre = main.minDiamondOre;

                        if (ratio > maxRatio && stoneMined > minStone && diamondOreMined > minDiamondOre) {
                            sender.sendMessage(ChatColor.RED + "Player may be using XRAY! Ratio: " + ratio);
                        }
                    } else {
                        sender.sendMessage("Blocks mined by: " + target.getName());
                        sender.sendMessage("Diamond Ore: " + diamondOreMined);
                        sender.sendMessage("Stone: " + stoneMined);
                        sender.sendMessage("Not enough data yet to display ratio!");
                    }


                } else if (args[0].equalsIgnoreCase("reload")) {
                    main.reloadConfig();
                    sender.sendMessage("Config Reloaded!");
                } else {
                    sender.sendMessage("Invalid Arguments! Usage: /antixrayplus [player] | /antixrayplus reload");
                }

            } else {
                sender.sendMessage("Invalid Arguments! Usage: /antixrayplus [player]");
            }
        }
        return false;
    }
}