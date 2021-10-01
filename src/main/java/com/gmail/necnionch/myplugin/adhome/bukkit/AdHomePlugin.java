package com.gmail.necnionch.myplugin.adhome.bukkit;

import com.gmail.necnionch.myplugin.adhome.bukkit.hooks.WorldDisplayNameBridge;
import com.gmail.necnionch.myplugin.adhome.bukkit.hooks.WorldRegenListener;
import com.gmail.necnionch.myplugin.adhome.common.MetricsLite;
import com.gmail.necnionch.myplugin.adhome.common.UpdateChecker;
import com.gmail.necnionch.myplugin.adhome.common.Utils;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AdHomePlugin extends JavaPlugin {
    Config config = new Config(this);
    DataStore store = new DataStore(this);
    MessageUtil mUtil = new MessageUtil(this);
    UpdateChecker updateChecker;
    private AdHomeAPI api = null;
    boolean useSpigotSend = false;
//    N8PageViewAPI pvApi = null;
    Map<Player, Set<String>> cachedHomeNames = new HashMap<>();
    private Integer autoSaveTaskId = null;
    MultiverseCoreBridge mvBridge = null;
    final WorldDisplayNameBridge worldDisplays = new WorldDisplayNameBridge(getLogger());

    @Override
    public void onEnable() {
        if (! config.convert_and_load("config.yml")) {
            getLogger().severe("設定ファイルのロードに失敗しました。");
            setEnabled(false);
            return;
        }
        if (! store.convert_and_load("home.yml")) {
            getLogger().severe("データファイルのロードに失敗しました。");
            setEnabled(false);
            return;
        }

        config.header(null);
        config.addHeaderText("version-check: true (added: v1.2.0)",
                "バージョンチェック");
        config.addHeaderText("use-clickable-message: true",
                "クリック＆マウスホバー可能メッセージを使う",
                "※ Spigot 1.7以降で利用可能です。");
        config.addHeaderText("use-multiverse-core: true (added: v1.2.0)",
                "MultiverseCoreからワールドの別名を取得する");
        config.addHeaderText("use-worlddisplayname: true (added: v1.3.0)",
                "MultiverseCoreからワールドの別名を取得する");
        config.addHeaderText("auto-save-time-minutes: 15",
                "ホームデータの自動セーブ (分)",
                "※ 0を指定すると自動セーブは無効です");
        config.addHeaderText("whitelist-world:",
                "実行できるワールドの指定 (以下: 例)",
                "  set-home: all  # 全てのワールドでsethome可能",
                "  set-home: [\"world\", \"resources\"]  # worldとresourcesワールドでsethome可能");
        config.addHeaderText("world-name: {\"world\": \"メインワールド\"}",
                "ワールドのカスタム名");
        config.addHeaderText("message:",
                "You can change the command message, but some changes are not applied if use-clickable-message is enabled.",
                "コマンドメッセージを変更できますが、use-clickable-messageが有効の場合、一部は変更が適用されません。");
        config.saveHeaderIfNotContains(true);

        updateChecker = new UpdateChecker(this, 72403, "adhome.notify.update", "&7[&aAdHome&7] ");

        cachedHomeNames.clear();
        for (Player p : getServer().getOnlinePlayers()) {
            try { cachedHomeNames.put(p, store.getAllHomeNames(p)); } catch (Exception ignored) {}
        }

        getCommand("adhome").setExecutor(new MainCommandExecutor(this));
        getCommand("home-teleport").setExecutor(new TeleportCommandExecutor(this));
        getCommand("home-set").setExecutor(new SetCommandExecutor(this));
        getCommand("home-delete").setExecutor(new DeleteCommandExecutor(this));
        getCommand("home-list").setExecutor(new ListCommandExecutor(this));
        getCommand("home-info").setExecutor(new InfoCommandExecutor(this));
        getServer().getPluginManager().registerEvents(new EventListener(this), this);

        try {
            Class.forName("org.bukkit.entity.Player$Spigot");
//            Class.forName("org.bukkit.command.CommandSender$Spigot");

            useSpigotSend = true;
//            useSpigotSend = Utils.allowedServerVersion("1.10", "1.11", "1.12", "1.13", "1.14");
        } catch (ClassNotFoundException e) {
            useSpigotSend = false;
        }
//        if (!useSpigotSend && config.useClickableMessage()) {
//            getLogger().info("ReadOnlyMessageを使用します。");
//        }

//        Plugin pl = Bukkit.getPluginManager().getPlugin("N8PageView");
//        if (pl instanceof N8PageViewPlugin) {
//            pvApi = ((N8PageViewPlugin) pl).getPageViewAPI();
//            if (config.useN8PageView())
//                getLogger().info("N8PageViewと連携しました。");
//        }

        PluginManager mgr = Bukkit.getPluginManager();


        Plugin temp = mgr.getPlugin("Multiverse-Core");
        if (temp instanceof MultiverseCore) {
            mvBridge = new MultiverseCoreBridge(this, temp);
        }

        if (worldDisplays.init(mgr.getPlugin("WorldDisplayName")))
            getLogger().info("Hooked to WorldDisplayName");


        startAutoSaveTask();
        api = new AdHomeAPI(this);


        if (mgr.isPluginEnabled("LOWorldRegenerator")) {
            try {
                mgr.registerEvents(new WorldRegenListener(api, config), this);
                getLogger().info("Hooked to LOWorldRegenerator");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }


        if (config.versionCheck())
            updateChecker.checkForUpdate();

        getLogger().info("Thank you for using this plugin!");

        new MetricsLite(this, 10035);

    }

    @Override
    public void onDisable() {
        api = null;
        stopAutoSaveTask();
        store.save();
    }

    void sendMessage(String msg, CommandSender s) {
        String prefix = config.getPrefix();
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + msg));
    }

    private void startAutoSaveTask() {
        stopAutoSaveTask();
        Integer autoSaveTime = config.getAutoSaveTime();
        if (autoSaveTime != null && autoSaveTime > 0) {
            autoSaveTime = autoSaveTime * 60 * 20;
            autoSaveTaskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
                if(store.modified)
                    if(!store.save())
                        getLogger().severe("自動セーブできませんでした。");
            }, autoSaveTime, autoSaveTime);
        }
    }

    private void stopAutoSaveTask() {
        if (autoSaveTaskId != null)
            getServer().getScheduler().cancelTask(autoSaveTaskId);
    }

    boolean safeReloadConfig() {
        boolean result = config.load();
        if (result)
            startAutoSaveTask();
        return result;
    }

    public AdHomeAPI getAdHomeApi() throws Exception {
        if (api != null) {
            return api;
        } else {
            throw new Exception("API is unavailable!");
        }
    }



    public Integer getSetHomeLimit(Player player) {
        if (player.hasPermission("adhome.limit.*"))
            return Integer.MAX_VALUE;

        Pattern regex = Pattern.compile("^adhome\\.limit\\.(\\d+)$");
        Integer count = null;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            Matcher m = regex.matcher(info.getPermission());
            if (m.find()) {
                int c;
                try {
                    c = Integer.parseInt(m.group(1));
                } catch (NumberFormatException e) {
                    continue;
                }
                if (count == null || count < c)
                    count = c;

            }
        }
        return count;
    }


}
