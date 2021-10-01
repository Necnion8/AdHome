package com.gmail.necnionch.myplugin.adhome.bukkit.hooks;

import com.gmail.necnionch.myplugin.adhome.bukkit.AdHomeAPI;
import com.gmail.necnionch.myplugin.adhome.bukkit.Config;
import com.gmail.necnionch.myplugin.adhome.bukkit.MyHome;
import com.gmail.necnionch.myplugin.worldregenerator.bukkit.events.WorldRegeneratedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class WorldRegenListener implements Listener {
    private final AdHomeAPI homes;
    private final Config config;

    public WorldRegenListener(AdHomeAPI homes, Config config) {
        this.homes = homes;
        this.config = config;
    }


    @EventHandler
    public void onGenerated(WorldRegeneratedEvent event) {
        if (!config.useWorldRegenerator())
            return;

        String worldName = event.getWorld().getName();

        for (UUID user : homes.getUsers()) {
            for (MyHome home : homes.getAllHomes(user)) {
                if (worldName.equals(home.getWorldName()))
                    homes.deleteHome(user, home);
            }
        }

    }


}
