package com.orf9.antixrayplus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class commandAntiXrayPlus implements CommandExecutor {
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

                        int stoneMined = (int) main.getPlayerData().get(uuid + ".stoneMined");
                        int diamondOreMined = (int) main.getPlayerData().get(uuid + ".diamondOreMined");

                        if (!(diamondOreMined == 0) && !(stoneMined == 0)) {
                            double ratio = (double) main.getPlayerData().get(uuid + ".ratio");

                            player.sendMessage("Blocks mined by: " + ChatColor.GREEN + target.getName());
                            player.sendMessage("Diamond Ore: " + diamondOreMined);
                            player.sendMessage("Stone: " + stoneMined);
                            player.sendMessage("Ratio of Diamond to Stone: " + ratio);

                            double maxRatio = main.getConfig().getDouble("MaxRatio");

                            int minStone = main.getConfig().getInt("StoneMinimum");
                            int minDiamondOre = main.getConfig().getInt("DiamondMinimum");

                            if (ratio > maxRatio && stoneMined > minStone && diamondOreMined > minDiamondOre) {
                                player.sendMessage(ChatColor.RED + "Player may be using XRAY! Ratio: " + ratio);
                            }
                        }else {
                            player.sendMessage("Blocks mined by: " + ChatColor.GREEN + target.getName());
                            player.sendMessage("Diamond Ore: " + diamondOreMined);
                            player.sendMessage("Stone: " + stoneMined);
                            player.sendMessage(ChatColor.RED +"Not enough data yet to display ratio!");
                        }



                    } else {
                        player.sendMessage("Invalid Arguments! Usage: /antixrayplus [player]");
                    }

                } else {
                    player.sendMessage("Invalid Arguments! Usage: /antixrayplus [player]");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
        } else {
            CommandSender player = sender;

                if (args.length == 1) {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target != null) {
                        String uuid = target.getUniqueId().toString();

                        int stoneMined = (int) main.getPlayerData().get(uuid + ".stoneMined");
                        int diamondOreMined = (int) main.getPlayerData().get(uuid + ".diamondOreMined");

                        if (!(diamondOreMined == 0) && !(stoneMined == 0)) {
                            double ratio = (double) main.getPlayerData().get(uuid + ".ratio");

                            player.sendMessage("Blocks mined by: " + target.getName());
                            player.sendMessage("Diamond Ore: " + diamondOreMined);
                            player.sendMessage("Stone: " + stoneMined);
                            player.sendMessage("Ratio of Diamond to Stone: " + ratio);

                            double maxRatio = main.getConfig().getDouble("maxRatio");

                            int minStone = main.getConfig().getInt("stoneMinimum");
                            int minDiamondOre = main.getConfig().getInt("DiamondMinimum");

                            if (minStone == 0){
                                minStone = 1024;
                            }
                            if (minDiamondOre == 0){
                                minDiamondOre = 32;
                            }
                            if (maxRatio == 0){
                                maxRatio = 0.002;
                            }

                            if (ratio > maxRatio && stoneMined > minStone && diamondOreMined > minDiamondOre) {
                                player.sendMessage(ChatColor.RED + "Player may be using XRAY! Ratio: " + ratio);
                            }
                        }else {
                            player.sendMessage("Blocks mined by: " + target.getName());
                            player.sendMessage("Diamond Ore: " + diamondOreMined);
                            player.sendMessage("Stone: " + stoneMined);
                            player.sendMessage("Not enough data yet to display ratio!");
                        }



                    } else {
                        player.sendMessage("You must specify a valid player!");
                    }

                } else {
                    player.sendMessage("Invalid Arguments! Usage: /antixrayplus [player]");
                }
        }
        return false;
    }
}

