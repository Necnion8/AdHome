package com.gmail.necnionch.myplugin.adhome.bukkit;

import com.gmail.necnionch.myplugin.adhome.common.BukkitConfigDriver;
import com.google.common.base.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Config extends BukkitConfigDriver {
    private AdHomePlugin plugin;
    private final Integer configVersion = 1;

    public Config(AdHomePlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    public String getPrefix() {
        return config.getString("command-prefix", "&7[&aAdHome&7] ");
    }

    public boolean useClickableMessage() {
        return config.getBoolean("use-clickable-message", true);
    }

    public boolean useMultiverseCore() {
        return config.getBoolean("use-multiverse-core", true);
    }

    public boolean useWorldDisplayName() {
        return config.getBoolean("use-worlddisplayname", true);
    }

    public boolean useWorldRegenerator() {
        return config.getBoolean("use-lo-worldregenerator", true);
    }

    public boolean useN8PageView() {
        return config.getBoolean("use-n8-pageview", true);
    }

    public boolean allowSetHomeWorld(String worldName) {
        ConfigurationSection whitelistWorld = config.getConfigurationSection("whitelist-world");
        if (whitelistWorld == null) return false;
        Object setHome = whitelistWorld.get("set-home");

        if ((setHome instanceof String) && ((String) setHome).equalsIgnoreCase("all")) {
            return true;
        } else {
            return whitelistWorld.getStringList("set-home").contains(worldName);
        }
    }

    public boolean allowTeleportHomeWorld(String worldName) {
        ConfigurationSection whitelistWorld = config.getConfigurationSection("whitelist-world");
        if (whitelistWorld == null) return false;
        Object teleportHome = whitelistWorld.get("teleport-home");

        if ((teleportHome instanceof String) && ((String) teleportHome).equalsIgnoreCase("all")) {
            return true;
        } else {
            return whitelistWorld.getStringList("teleport-home").contains(worldName);
        }
    }

    public String getWorldDisplay(String worldName) {
        String customName = config.getString("world-name." + worldName, "");
        if (customName != null && !customName.isEmpty())
            return customName;

        if (useWorldDisplayName()) {
            customName = plugin.worldDisplays.getDisplayName(worldName);
            if (customName != null)
                return customName;
        }

        if (plugin.mvBridge != null && plugin.mvBridge.enabled()) {
            customName = plugin.mvBridge.getWorldAlias(worldName);
            if (customName != null && !customName.isEmpty())
                return customName;
        }
        return worldName;
    }

    public String getWorldDisplay(World world) {
        String customName = config.getString("world-name." + world.getName(), "");
        if (customName != null && !customName.isEmpty())
            return customName;

        if (useWorldDisplayName()) {
            customName = plugin.worldDisplays.getDisplayName(world.getName());
            if (customName != null)
                return customName;
        }

        if (plugin.mvBridge != null && plugin.mvBridge.enabled()) {
            customName = plugin.mvBridge.getWorldAlias(world);
            if (customName != null && !customName.isEmpty())
                return customName;
        }
        return world.getName();
    }

    public String getDefaultHomeName() {
        return config.getString("message.others.default-home", "デフォルトホーム");
    }

    public String getMessage(String keys) {
        String t = config.getString("message." + keys, "");
        if (t == null || t.isEmpty()) {
            t = keys;
        }
        return t;
    }

    boolean convert_and_load(String targetFileName) {
        File configFile = new File(plugin.getDataFolder(), targetFileName);
        boolean result = true;
        if (configFile.exists()) {
            Integer configVersion = null;

            try (InputStreamReader stream = new InputStreamReader(new FileInputStream(configFile), Charsets.UTF_8)) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(stream);
                configVersion = config.getInt("config-version", 0);
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
            }

            if (result && (this.configVersion > configVersion)) {
                result = backup_file(configFile, "config-v" + configVersion + ".yml");
            }
        }

        return result && load();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean backup_file(File target, String renamed) {
        File backupFolder = new File(plugin.getDataFolder(), "old-backup");
        if (!backupFolder.exists())
            backupFolder.mkdir();

        Path backupDest = Paths.get(backupFolder.toString(), renamed);
        try {
            Files.move(target.toPath(), backupDest);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Integer getAutoSaveTime() {
        return config.getInt("auto-save-time-minutes", 15);
    }

    public boolean versionCheck() {return config.getBoolean("version-check", true);}
}