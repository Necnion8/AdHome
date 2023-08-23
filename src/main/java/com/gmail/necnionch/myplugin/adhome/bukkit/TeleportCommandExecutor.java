package com.gmail.necnionch.myplugin.adhome.bukkit;

import com.gmail.necnionch.myplugin.adhome.common.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TeleportCommandExecutor implements CommandExecutor, TabCompleter {
    private AdHomePlugin plugin;
    private Config config;
    private DataStore store;
    private MessageUtil mUtil;

    TeleportCommandExecutor(AdHomePlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.config;
        this.store = plugin.store;
        this.mUtil = plugin.mUtil;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.sendMessage("&cプレイヤーのみ実行可能なコマンドです。", sender);
            return true;
        }

        if (!sender.hasPermission("adhome.command.home")) {
            mUtil.sendPermissionError(sender);
            return true;
        }

        Player player = (Player) sender;

        if (!(config.allowTeleportHomeWorld(player.getWorld().getName()) || sender.hasPermission("adhome.bypass.whitelist-world"))) {
            mUtil.sendNotAllowedWorld(player);
            return true;
        }

        String homeName = null;
        if (args.length >= 1)
            homeName = args[0];

        MyHome home = store.getHome(player, homeName);

        if (home == null) {
            mUtil.sendHomeNotExists(player);
        } else {
            Location loc = home.getLocation();
            if (loc != null) {
                player.teleport(loc);
                mUtil.sendTeleportHome(player, home);
            } else {
                mUtil.sendTeleportUnavailableWorld(player, home);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player && sender.hasPermission("adhome.command.home")) {
            Player player = (Player) sender;
            if (args.length == 1 && plugin.cachedHomeNames.containsKey(player)) {
                return Utils.generateTab(args[0], plugin.cachedHomeNames.get(player).toArray(new String[0]));
            }
        }
        return Collections.emptyList();
    }
}
