package com.gmail.necnionch.myplugin.adhome.bukkit.hooks;

import com.gmail.necnionch.myplugin.worlddisplayname.bukkit.WorldDisplayName;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class WorldDisplayNameBridge {
    private final Logger logger;
    private WorldDisplayName names;

    public WorldDisplayNameBridge(Logger logger) {
        this.logger = logger;
    }

    public boolean init(Plugin plugin) {
        try {
            Class.forName("com.gmail.necnionch.myplugin.worlddisplayname.bukkit.WorldDisplayName");

            if (plugin instanceof WorldDisplayName && plugin.isEnabled()) {
                names = ((WorldDisplayName) plugin);
                return true;
            }
        } catch (ClassNotFoundException ignored) {
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getDisplayName(String worldName) {
        return (names != null) ? names.getWorldDisplay(worldName) : null;
    }

}
