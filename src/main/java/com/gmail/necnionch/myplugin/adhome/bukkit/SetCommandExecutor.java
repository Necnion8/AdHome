package com.gmail.necnionch.myplugin.adhome.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SetCommandExecutor implements CommandExecutor, TabCompleter {
    private AdHomePlugin plugin;
    private Config config;
    private DataStore store;
    private MessageUtil mUtil;

    SetCommandExecutor(AdHomePlugin plugin) {
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

        if (!sender.hasPermission("adhome.command.sethome")) {
            mUtil.sendPermissionError(sender);
            return true;
        }

        Player player = (Player) sender;

        if (!(config.allowSetHomeWorld(player.getWorld().getName()) || sender.hasPermission("adhome.bypass.whitelist-world"))) {
            mUtil.sendNotAllowedWorld(player);
            return true;
        }


        Integer limit = plugin.getSetHomeLimit(player);
        int exists = store.getAllHomeNames(player.getUniqueId()).size();
        if (limit != null && limit <= exists) {
            mUtil.sendSetHomeLimited(player);
            return true;
        }


        String homeName;
        if (args.length == 0) {
            homeName = null;
        } else if (args.length == 1)
            homeName = args[0];
        else {
            mUtil.sendSetHomeContainsSpace(player);
            return true;
        }

        MyHome home = store.getHome(player, homeName);

        if (home == null) {
            home = new MyHome(homeName, player.getLocation());
            store.setHome(player, home);
            mUtil.sendSetHome(player, home);
            if (home.getName() != null)
                plugin.cachedHomeNames.get(player).add(home.getName());
        } else {
            mUtil.sendSetHomeAlreadyExists(player, home);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
