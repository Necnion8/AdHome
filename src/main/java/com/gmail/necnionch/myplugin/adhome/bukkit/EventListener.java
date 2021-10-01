package com.gmail.necnionch.myplugin.adhome.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;

public class EventListener implements Listener {
    private AdHomePlugin plugin;

    EventListener(AdHomePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Set<String> homes;
        try {
            homes = plugin.store.getAllHomeNames(event.getPlayer());
        } catch (Exception e) {
            plugin.getLogger().warning("Home一覧を取得できませんでした: " + e.getLocalizedMessage());
            homes = new HashSet<>();
        }
        plugin.cachedHomeNames.put(player, homes);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        try {
            plugin.cachedHomeNames.remove(player);
        } catch (Exception e) {
            plugin.getLogger().warning("Home一覧のキャッシュを削除できませんでした: " + e.getLocalizedMessage());
        }
    }
}
