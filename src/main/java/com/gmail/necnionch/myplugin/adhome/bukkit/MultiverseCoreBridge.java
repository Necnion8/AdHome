package com.gmail.necnionch.myplugin.adhome.bukkit;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.Core;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;


public class MultiverseCoreBridge {
    private AdHomePlugin adhome;
    private Core mvc = null;

    public MultiverseCoreBridge(AdHomePlugin adHome, Plugin plugin) {
        this.adhome = adHome;
        if (plugin instanceof MultiverseCore && plugin.isEnabled()) {
            try {
                mvc = ((MultiverseCore) plugin).getCore();
            } catch (Exception e) {
                adhome.getLogger().warning(plugin.getName() + "と連携できませんでした。");
                e.printStackTrace();
            }
        }
    }

    public boolean available() {return mvc != null;}

    public boolean enabled() {return available() && adhome.config.useMultiverseCore();}

    public String getWorldAlias(World world) {
        if (!available()) return null;

        MultiverseWorld mvWorld = mvc.getMVWorldManager().getMVWorld(world);
        if (mvWorld != null)
            return mvWorld.getAlias();
        return world.getName();
    }

    public String getWorldAlias(String worldName) {
        if (!available()) return worldName;

        MultiverseWorld mvWorld = mvc.getMVWorldManager().getMVWorld(worldName);
        if (mvWorld != null)
            return mvWorld.getAlias();
        return worldName;
    }
}
