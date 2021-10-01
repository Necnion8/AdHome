package com.gmail.necnionch.myplugin.adhome.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ListCommandExecutor implements CommandExecutor, TabCompleter {
    private AdHomePlugin plugin;
    private MessageUtil mUtil;

    ListCommandExecutor(AdHomePlugin plugin) {
        this.plugin = plugin;
        this.mUtil = plugin.mUtil;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.sendMessage("&cプレイヤーのみ実行可能なコマンドです。", sender);
            return true;
        }

        if (!sender.hasPermission("adhome.command.listhome")) {
            mUtil.sendPermissionError(sender);
            return true;
        }

        Player player = (Player) sender;
        mUtil.sendListHome(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
