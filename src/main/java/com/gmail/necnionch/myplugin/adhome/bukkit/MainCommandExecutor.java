package com.gmail.necnionch.myplugin.adhome.bukkit;

import com.gmail.necnionch.myplugin.adhome.common.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Collections;
import java.util.List;

public class MainCommandExecutor implements CommandExecutor, TabCompleter {
    private AdHomePlugin plugin;
    private MessageUtil mUtil;

    MainCommandExecutor(AdHomePlugin plugin) {
        this.plugin = plugin;
        this.mUtil = plugin.mUtil;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Utils.checkArgument(args, "help", 0)) {
            plugin.sendMessage("コマンド一覧：", sender);
            if (sender instanceof Player) {
                sender.sendMessage("§7 > §6/home [ホーム名]     §7/  §fホームにテレポート §7(home-teleport)");
                sender.sendMessage("§7 > §6/shome [ホーム名]    §7/  §f現在位置をセット §7(home-set)");
                sender.sendMessage("§7 > §6/dhome [ホーム名]    §7/  §fホームを削除 §7(home-delete)");
                sender.sendMessage("§7 > §6/lhome               §7/  §fホームを全て表示 §7(home-list)");
                sender.sendMessage("§7 > §6/ihome  [ホーム名]   §7/  §fホーム情報を表示 §7(home-info)");
            }
            if (sender.hasPermission("adhome.command.reload")) {
                sender.sendMessage("§7 > §6/adhome reload       §7/  §f設定を再読み込みします");
            }
            sender.sendMessage("§7 > §6/adhome help         §7/  §fコマンドを一覧表示します");
        } else if (Utils.checkArgument(args, "reload", 0)) {
            if (sender.hasPermission("adhome.command.reload")) {
                if (plugin.safeReloadConfig()) {
                    plugin.sendMessage("&7再読み込みしました！", sender);
                } else {
                    plugin.sendMessage("&cエラーが発生しました。", sender);
                }
            } else {
                mUtil.sendPermissionError(sender);
            }

        } else {
            PluginDescriptionFile d = plugin.getDescription();
            plugin.sendMessage("§7/adhome help  §8§l|  §3version " + d.getVersion(), sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            if (sender.hasPermission("adhome.command.reload")) {
                return Utils.generateTab(args[0], "help", "reload");
            }
            return Utils.generateTab(args[0], "help");
        } else {
            return Collections.emptyList();
        }
    }
}
