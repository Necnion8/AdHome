package com.gmail.necnionch.myplugin.adhome.common;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;


// version 1
public class UpdateChecker {
    private final JavaPlugin javaPlugin;
    private final String localPluginVersion;
    private String spigotPluginVersion;

    //Constants. Customize to your liking.
//    private static final int ID = 44876; //The ID of your resource. Can be found in the resource URL.
//    private static final String ERR_MSG = "&cUpdate checker failed!";
//    private static final String UPDATE_MSG = "&fA new update is available at:&b https://www.spigotmc.org/resources/" + ID + "/updates";
    //PermissionDefault.FALSE == OPs need the permission to be notified.
    //PermissionDefault.TRUE == all OPs are notified regardless of having the permission.
//    private static final Permission UPDATE_PERM = new Permission("yourplugin.update", PermissionDefault.FALSE);

    private int checkInterval = 30;  // in minutes
    private int resourceId;
    private String prefix = null;
    private String notifyPermission = null;


    public UpdateChecker(final JavaPlugin javaPlugin, int resourceId) {
        this.javaPlugin = javaPlugin;
        this.localPluginVersion = javaPlugin.getDescription().getVersion();
        this.resourceId = resourceId;
    }

    public UpdateChecker(final JavaPlugin javaPlugin, int resourceId, String notifyPermission, String prefix) {
        this.javaPlugin = javaPlugin;
        this.localPluginVersion = javaPlugin.getDescription().getVersion();
        this.resourceId = resourceId;
        this.notifyPermission = notifyPermission;
        this.prefix = prefix!=null? prefix: "";
    }

    public void setCheckInterval(int minutes) {this.checkInterval = minutes;}

    public void checkForUpdate() {
        new BukkitRunnable() {
            @Override
            public void run() {
                //The request is executed asynchronously as to not block the main thread.
                Bukkit.getScheduler().runTaskAsynchronously(javaPlugin, () -> {
                    //Request the current version of your plugin on SpigotMC.
                    try {
                        final HttpsURLConnection connection = (HttpsURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openConnection();
                        connection.setRequestMethod("GET");
                        spigotPluginVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                    } catch (FileNotFoundException e) {
                        javaPlugin.getLogger().warning("Update checker failed! (plugin removed?)");
                        cancel();
                        return;
                    } catch (final IOException e) {
                        javaPlugin.getLogger().warning("Update checker failed! (" + e.getClass().getName() + ": " + e.getLocalizedMessage() + ")");
                        cancel();
                        return;
                    }

                    //Check if the requested version is the same as the one in your plugin.yml.
                    if (localPluginVersion.equals(spigotPluginVersion)) return;
                    if (spigotPluginVersion.toLowerCase().contains("snapshot")) return;

                    javaPlugin.getLogger().info("New version found: (v" + spigotPluginVersion + ") https://www.spigotmc.org/resources/" + resourceId + "/updates");

                    //Register the PlayerJoinEvent
                    if (notifyPermission != null)
                        Bukkit.getScheduler().runTask(javaPlugin, () -> Bukkit.getPluginManager().registerEvents(new Listener() {
                            @EventHandler(priority = EventPriority.MONITOR)
                            public void onPlayerJoin(final PlayerJoinEvent event) {
                                final Player player = event.getPlayer();
                                if (!player.hasPermission(notifyPermission)) return;
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&fNew version &e" + spigotPluginVersion + "&f is available at:"));
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&b&ohttps://www.spigotmc.org/resources/" + resourceId + "/updates"));
                            }
                        }, javaPlugin));

                    cancel(); //Cancel the runnable as an update has been found.
                });
            }
        }.runTaskTimer(javaPlugin, 0, (long) checkInterval * 60 * 20);
    }
}