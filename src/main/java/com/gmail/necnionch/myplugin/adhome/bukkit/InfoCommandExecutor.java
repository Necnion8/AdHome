package com.gmail.necnionch.myplugin.adhome.bukkit;

import com.gmail.necnionch.myplugin.adhome.common.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class InfoCommandExecutor implements CommandExecutor, TabCompleter {
    private AdHomePlugin plugin;
    private DataStore store;
    private MessageUtil mUtil;

    InfoCommandExecutor(AdHomePlugin plugin) {
        this.plugin = plugin;
        this.store = plugin.store;
        this.mUtil = plugin.mUtil;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.sendMessage("&cプレイヤーのみ実行可能なコマンドです。", sender);
            return true;
        }

        if (!sender.hasPermission("adhome.command.infohome")) {
            mUtil.sendPermissionError(sender);
            return true;
        }

        Player player = (Player) sender;
        String homeName = null;
        if (args.length >= 1)
            homeName = args[0];

        MyHome home = store.getHome(player, homeName);

        if (home == null) {
            mUtil.sendHomeNotExists(player);
        } else {
            mUtil.sendInfoHome(player, home);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player && sender.hasPermission("adhome.command.infohome")) {
            Player player = (Player) sender;
            if (args.length == 1 && plugin.cachedHomeNames.containsKey(player)) {
                return Utils.generateTab(args[0], plugin.cachedHomeNames.get(player).toArray(new String[0]));
            }
        }
        return Collections.emptyList();
    }
}
