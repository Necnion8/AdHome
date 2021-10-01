package com.gmail.necnionch.myplugin.adhome.bukkit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

class MessageUtil {
    private AdHomePlugin plugin;
    private Config config;
    private DataStore store;

    MessageUtil(AdHomePlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.config;
        this.store = plugin.store;
    }

    private boolean useClickable() {
        return plugin.useSpigotSend && config.useClickableMessage();
    }

    private String getPrefix() {
        return config.getPrefix();
    }

    void sendSetHome(Player p, MyHome h) {
        String name = (h.getName()!=null)? h.getName(): config.getDefaultHomeName();

        if (useClickable()) {
            TextComponent hT = new TextComponent(name);
            hT.setClickEvent(getClickEvent(h));
            hT.setHoverEvent(getHoverEvent(h));
            if (h.getName()!=null)
                hT.setColor(ChatColor.YELLOW);
            TextComponent t = new TextComponent(toColored(getPrefix()));
            t.addExtra(hT);
            t.addExtra(new TextComponent("をセットしました！"));
            t.setColor(ChatColor.GOLD);

            p.spigot().sendMessage(t);
        } else {
            String key = (h.getName()!=null)? "home-set.name": "home-set.no-name";
            p.sendMessage(toColored(getPrefix() + (config.getMessage(key).replaceAll("\\{home}", name))));
        }
    }

    void sendSetHomeAlreadyExists(Player p, MyHome h) {
        if (useClickable()) {
            TextComponent hT = new TextComponent("ホーム名");
            hT.setClickEvent(getClickEvent(h));
            hT.setHoverEvent(getHoverEvent(h));
            TextComponent t = new TextComponent(toColored(getPrefix()));
            t.addExtra(new TextComponent("この"));
            t.addExtra(hT);
            t.addExtra(new TextComponent("は既に使われています。"));
            t.setColor(ChatColor.RED);
            p.spigot().sendMessage(t);
        } else {
            p.sendMessage(toColored(getPrefix() + config.getMessage("home-set.already-exists")));
        }
    }

    void sendSetHomeContainsSpace(Player p) {
        if (useClickable()) {
            TextComponent t = new TextComponent(toColored(getPrefix()));
            t.addExtra("ホーム名に空白文字は使用できません。");
            t.setColor(ChatColor.RED);
            p.spigot().sendMessage(t);
        } else {
            p.sendMessage(toColored(getPrefix() + config.getMessage("home-set.contains-space")));
        }
    }

    void sendSetHomeLimited(Player p) {
        p.sendMessage(toColored(getPrefix() + config.getMessage("home-set.limited-homes")));
    }

    void sendTeleportHome(Player p, MyHome h) {
        String name = (h.getName()!=null)? h.getName(): config.getDefaultHomeName();

        if (useClickable()) {
            TextComponent hT = new TextComponent(name);
            hT.setClickEvent(getClickEvent(h));
            hT.setHoverEvent(getHoverEvent(h));
            TextComponent t = new TextComponent(toColored(getPrefix()));
            t.addExtra(hT);
            t.addExtra("にテレポートしました。");
            t.setColor(ChatColor.GRAY);
            p.spigot().sendMessage(t);

        } else {
            String key = (h.getName()!=null)? "home-teleport.name": "home-teleport.no-name";
            p.sendMessage(toColored(getPrefix() + (config.getMessage(key).replaceAll("\\{home}", name))));
        }
    }

    void sendTeleportUnavailableWorld(Player p, MyHome h) {
        if (useClickable()) {
            TextComponent t = new TextComponent(toColored(getPrefix()));
            t.addExtra(config.getWorldDisplay(h.getWorldName()) + "が存在しません。");
            t.setColor(ChatColor.RED);
            p.spigot().sendMessage(t);
        } else {
            String key = "home-teleport.unavailable-world";
            p.sendMessage(toColored(getPrefix() + (config.getMessage(key).replaceAll("\\{world}", config.getWorldDisplay(h.getWorldName())))));
        }
    }

    void sendDeleteHome(Player p, MyHome h) {
        String name = (h.getName()!=null)? h.getName(): config.getDefaultHomeName();
        if (useClickable()) {
            TextComponent hT = new TextComponent(name);
//            hT.setClickEvent(getClickEvent(h));
//            hT.setHoverEvent(getHoverEvent(h));
            if (h.getName()!=null)
                hT.setColor(ChatColor.YELLOW);
            TextComponent t = new TextComponent(toColored(getPrefix()));
            t.addExtra(hT);
            t.addExtra("を削除しました。");
            t.setColor(ChatColor.GOLD);
            p.spigot().sendMessage(t);
        } else {
            String key = (h.getName()!=null)? "home-delete.name": "home-delete.no-name";
            p.sendMessage(toColored(getPrefix() + (config.getMessage(key).replaceAll("\\{home}", name))));
        }
    }

    void sendListHome(Player p) {
        List<MyHome> homes = store.getAllHomes(p);

        if (homes.isEmpty()) {
            p.sendMessage(toColored(getPrefix() + config.getMessage("home-list.none")));
            return;
        }

//        if (plugin.pvApi != null && config.useN8PageView()) {
//            PageViewBuilder pvb = plugin.pvApi.getPageViewBuilder(new TextComponent("ホーム一覧"), homes);

//            pvb.setFormatter(new PageViewFormatter() {
//                @Override
//                public TextComponent itemFormat(Object o) {
//                    MyHome h = (MyHome) o;
//                    TextComponent hT = new TextComponent(h.getName());
//                    hT.setColor(ChatColor.GOLD);
//                    TextComponent xT = new TextComponent(String.valueOf(h.getX().intValue()));
//                    xT.setColor(ChatColor.YELLOW);
//                    TextComponent yT = new TextComponent(String.valueOf(h.getY().intValue()));
//                    yT.setColor(ChatColor.YELLOW);
//                    TextComponent zT = new TextComponent(String.valueOf(h.getZ().intValue()));
//                    zT.setColor(ChatColor.YELLOW);
//                    TextComponent wT = new TextComponent(h.getWorldName());
//                    wT.setColor(ChatColor.YELLOW);
//
//                    TextComponent t = new TextComponent(
//                            hT, new TextComponent("  --  ( X: "), xT, new TextComponent(", Y: "), yT, new TextComponent(", Z: "), zT, new TextComponent(", "), wT, new TextComponent(" )")
//                    );
//                    t.setColor(ChatColor.GRAY);
//
//                    String command = (h.getName()!=null)? " "+h.getName(): "";
//                    t.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/home" + command));
//                    return t;
//                }
//            });
//
//            pvb.setShowButton(true);
//            pvb.setRowSize(1);
//            pvb.send(p);
//
//        } else if (useClickable()) {
        if (useClickable()) {
            TextComponent t = new TextComponent(toColored(getPrefix()));
            t.addExtra(getText("ホーム一覧： ", ChatColor.GOLD));

            TextComponent hT;
            int c = 0;
            for (MyHome h : homes) {
                hT = new TextComponent(h.getName());
                hT.setClickEvent(getClickEvent(h));
                hT.setHoverEvent(getHoverEvent(h));
                hT.setColor(ChatColor.YELLOW);
                t.addExtra(hT);
                c++;
                if (homes.size() > c)
                    t.addExtra(getText(", ", ChatColor.WHITE));
            }
            p.spigot().sendMessage(t);

        } else {
            StringBuilder sb = new StringBuilder(toColored(getPrefix()));
            sb.append(toColored(config.getMessage("home-list.display")));

            int c = 0;
            for (MyHome h : homes) {
                sb.append(toColored(config.getMessage("home-list.entry-color")));
                sb.append(config.getMessage(h.getName()));
                c++;
                if (homes.size() > c)
                    sb.append(toColored("&f, "));
            }
            p.sendMessage(sb.toString());
        }
    }

    void sendInfoHome(Player p, MyHome h) {
        String name = (h.getName()!=null)? h.getName(): config.getDefaultHomeName();

        if (useClickable()) {
            TextComponent hT = getText(name, ChatColor.GOLD);
            hT.setClickEvent(getClickEvent(h));
            hT.setHoverEvent(getHoverEvent(h));

            TextComponent xT = new TextComponent(String.valueOf(h.getX().intValue()));
            xT.setColor(ChatColor.YELLOW);
            TextComponent yT = new TextComponent(String.valueOf(h.getY().intValue()));
            yT.setColor(ChatColor.YELLOW);
            TextComponent zT = new TextComponent(String.valueOf(h.getZ().intValue()));
            zT.setColor(ChatColor.YELLOW);
            TextComponent wT = new TextComponent(config.getWorldDisplay(h.getWorldName()));
            if (h.getLocation()!=null) {
                wT.setColor(ChatColor.YELLOW);
            } else {
                wT.setColor(ChatColor.RED);
                wT.setItalic(true);
            }

            TextComponent t = new TextComponent(
                    new TextComponent(toColored(getPrefix())), getText("ホーム情報： ", ChatColor.GRAY), hT, new TextComponent("\n( "),
                    new TextComponent("X: "), xT, new TextComponent(", Y: "), yT, new TextComponent(", Z: "), zT, new TextComponent(", "), wT,
                    new TextComponent(" )")
            );
            t.setColor(ChatColor.GRAY);
            p.spigot().sendMessage(t);
        } else {
            String t = toColored(config.getPrefix() + config.getMessage("home-info.display") + config.getMessage("home-info.line"));
            String notFound = toColored((h.getLocation()==null)? config.getMessage("home-info.not-found"): "");
            t = t.replaceAll("\\{home}", name).replaceAll("\\{x}", String.valueOf(h.getX().intValue()))
                    .replaceAll("\\{y}", String.valueOf(h.getY().intValue())).replaceAll("\\{z}", String.valueOf(h.getZ().intValue()))
                    .replaceAll("\\{world}", config.getWorldDisplay(h.getWorldName())).replaceAll("\\{not-found}", notFound);

            p.sendMessage(t);
        }
    }

    void sendHomeNotExists(Player p) {
        p.sendMessage(toColored(getPrefix() + config.getMessage("others.not-exists")));
    }

    void sendPermissionError(CommandSender s) {
        s.sendMessage(toColored(getPrefix() + config.getMessage("others.permission-error")));
    }

    void sendNotAllowedWorld(Player p) {
        p.sendMessage(toColored(getPrefix() + config.getMessage("others.not-allowed-world")));
    }

    private ClickEvent getClickEvent(MyHome h) {
        String command = (h.getName()!=null)? " "+h.getName(): "";
        return new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/home" + command);
    }

    private HoverEvent getHoverEvent(MyHome h) {
//        String name = (h.getName()!=null)? h.getName(): config.getDefaultHomeName();

        TextComponent xT = new TextComponent(String.valueOf(h.getX().intValue()));
        xT.setColor(ChatColor.YELLOW);
        TextComponent yT = new TextComponent(String.valueOf(h.getY().intValue()));
        yT.setColor(ChatColor.YELLOW);
        TextComponent zT = new TextComponent(String.valueOf(h.getZ().intValue()));
        zT.setColor(ChatColor.YELLOW);
        TextComponent wT = new TextComponent(config.getWorldDisplay(h.getWorldName()));

        TextComponent wT2 = new TextComponent("");
        if (h.getLocation()!=null) {
            wT.setColor(ChatColor.YELLOW);
        } else {
            wT.setColor(ChatColor.RED);
            wT.setItalic(true);
            wT2.setText("\n( ワールドが存在しません。)");
            wT2.setColor(ChatColor.RED);
        }

        TextComponent t = new TextComponent(
                new TextComponent("X: "), xT, new TextComponent(", Y: "), yT, new TextComponent(", Z: "), zT, new TextComponent(", "), wT, wT2
        );
        t.setColor(ChatColor.GRAY);

        String command = (h.getName()!=null)? " "+h.getName(): "";
        t.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/home" + command));
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{t});
    }

    private TextComponent getText(String s, ChatColor c) {
        TextComponent tc = new TextComponent(s);
        tc.setColor(c);
        return tc;
    }

    private String toColored(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
