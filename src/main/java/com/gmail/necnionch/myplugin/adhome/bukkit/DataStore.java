package com.gmail.necnionch.myplugin.adhome.bukkit;

import com.gmail.necnionch.myplugin.adhome.common.BukkitConfigDriver;
import com.google.common.base.Charsets;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class DataStore extends BukkitConfigDriver {
    private AdHomePlugin plugin;
    private Logger logger;
    private Map<String, List<MyHome>> imported = new HashMap<>();
    boolean modified = false;

    @Override
    public boolean save() {
        boolean result = super.save();
        if (result)
            modified = false;
        return result;
    }

    DataStore(AdHomePlugin plugin) {
        super(plugin, "data-store.yml", "empty-file.yml");
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    MyHome getHome(Player player, String name) {
        return getHome(player.getUniqueId(), name);
    }

    MyHome getHome(UUID player, String name) {
        String path;
        if (name != null) {
            path = player + ".named." + name;
        } else {
            path = player + ".default";
        }
        String homeData = config.getString(path);
        if ((homeData != null) && (!homeData.isEmpty())) {
            try {
                return MyHome.deserialize(name, homeData);
            } catch (DeserializeException e) {
                logger.severe("MyHomeデータを生成できませんでした。(uuid: " + player + ", home: " + name + ")");
            }
        }
        return null;
    }

    void setHome(Player player, MyHome home) {
        setHome(player.getUniqueId(), home);
    }

    void setHome(UUID player, MyHome home) {
        setHome(player.toString(), home);
    }

    private void setHome(String uuid, MyHome home) {
        String path;
        if (home.getName() != null) {
            path = uuid + ".named." + home.getName();
        } else {
            path = uuid + ".default";
        }
        config.set(path, home.serialize());
        modified = true;
    }

    void deleteHome(Player player, String name) {
        deleteHome(player.getUniqueId(), name);
    }

    void deleteHome(UUID player, String name) {
        String path;
        if (name != null) {
            path = player + ".named." + name;
        } else {
            path = player + ".default";
        }
        config.set(path, null);
        modified = true;
    }

    List<MyHome> getAllHomes(Player player) {
        return getAllHomes(player.getUniqueId());
    }

    List<MyHome> getAllHomes(UUID player) {
        List<MyHome> homes = new ArrayList<>();
        ConfigurationSection named = config.getConfigurationSection(player + ".named");
        if (named != null) {
            MyHome h;
            for (String name : named.getKeys(false)) {
                h = getHome(player, name);
                if (h != null)
                    homes.add(h);
            }
        }
        return homes;
    }

    Set<String> getAllHomeNames(Player player) {
        return getAllHomeNames(player.getUniqueId());
    }

    Set<String> getAllHomeNames(UUID player) {
        ConfigurationSection named = config.getConfigurationSection(player.toString() + ".named");
        if (named != null) {
            return named.getKeys(false);
        } else {
            return new HashSet<>();
        }
    }

    public UUID[] getUsers() {
        List<UUID> users = new ArrayList<>();
        for (String key : config.getKeys(false)) {
            try {
                users.add(UUID.fromString(key));
            } catch (IllegalArgumentException ignored) {}
        }
        return users.toArray(new UUID[0]);
    }

    boolean convert_and_load(String targetFileName) {
        File configFile = new File(plugin.getDataFolder(), targetFileName);
        boolean result = true;
        imported.clear();

        if (configFile.exists()) {
            ConfigurationSection users = null;
            try (InputStreamReader stream = new InputStreamReader(new FileInputStream(configFile), Charsets.UTF_8)) {
                users = YamlConfiguration.loadConfiguration(stream).getConfigurationSection("users");

            } catch (IOException e) {
                e.printStackTrace();
                result = false;
            }

            try {
                if (result && users != null) {
                    for (String uuid : users.getKeys(false)) {
                        List<MyHome> importedUser = new ArrayList<>(Collections.emptyList());
                        imported.put(uuid, importedUser);

                        ConfigurationSection defaultHome = users.getConfigurationSection(uuid + ".noname");
                        MyHome h = loadOldConfig(null, defaultHome);
                        if (h != null)
                            importedUser.add(h);

                        ConfigurationSection homes = users.getConfigurationSection(uuid + ".home");
                        if (homes != null)
                            for (String name : homes.getKeys(false)) {
                                h = loadOldConfig(name, homes.getConfigurationSection(name));
                                importedUser.add(h);
                            }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }
            if (result)
                result = backup_file(configFile, "home.yml");
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

    private MyHome loadOldConfig(String name, ConfigurationSection c) {
        if (c == null) return null;
        if (c.isSet("X") && c.isSet("Y") && c.isSet("Z") && c.isSet("World")) {
            return new MyHome(
                    name,
                    c.getDouble("X"),
                    c.getDouble("Y"),
                    c.getDouble("Z"),
                    (float) c.getInt("Yaw", 0),
                    c.getString("World")
            );
        }
        return null;
    }

    @Override
    public boolean onLoaded(FileConfiguration config) {
        if (!imported.isEmpty()) {
            int entries = 0;
            for (Map.Entry<String, List<MyHome>> e : imported.entrySet()) {
                for (MyHome h : e.getValue()) {
                    setHome(e.getKey(), h);
                    entries += 1;
                }
            }
            logger.info("旧データからインポートしました。(件数: " + entries + ")");
            save();
            imported.clear();
        }
        imported = null;
        return true;
    }
}
