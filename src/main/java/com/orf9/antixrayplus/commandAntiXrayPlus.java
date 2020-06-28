package com.orf9.antixrayplus;

import org.bukkit.Bukkit;
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

            if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[0]);
                if(target != null) {
                    String uuid = target.getUniqueId().toString();

                    int stoneMined = (int) main.getPlayerData().get(uuid + ".stoneMined");
                    int diamondOreMined = (int) main.getPlayerData().get(uuid + ".diamondOreMined");
                    double ratio = (double) main.getPlayerData().get(uuid + ".ratio");

                    player.sendMessage("Stats for: " + target.getName());
                    player.sendMessage("diamond_ore mined: " + diamondOreMined);
                    player.sendMessage("stone mined: " + stoneMined);
                    player.sendMessage("Ratio of diamond_ore to stone: " + ratio);
                }else {
                    player.sendMessage("Invalid Arguments! Usage: /antixrayplus [player]");
                }

            } else {
                player.sendMessage("Invalid Arguments! Usage: /antixrayplus [player]");



            }
        } else {
            sender.sendMessage("This command can only be used in-game!");
        }
        return false;
    }
}
