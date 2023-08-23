package com.gmail.necnionch.myplugin.adhome.bukkit;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AdHomeAPI {
    private final AdHomePlugin adhome;

    public AdHomeAPI(AdHomePlugin adhome) {
        this.adhome = adhome;
    }

    /**
     * 指定されたワールド名に表示名が設定されていれば、その文字列に置換します。
     * @param worldName ワールド名
     */
    public String getWorldDisplay(String worldName) {
        return adhome.config.getWorldDisplay(worldName);
    }

    /**
     * プレイヤーのホームを取得します。
     * デフォルトホームを取得するには、nameをnullまたは空欄にします。
     * 存在しない場合は null を返します。
     * @param player プレイヤー
     * @param name ホーム名
     */
    public MyHome getHome(Player player, String name) {
        return adhome.store.getHome(player, name);
    }

    /**
     * プレイヤーのホームを取得します。
     * デフォルトホームを取得するには、nameをnullまたは空欄にします。
     * 存在しない場合は null を返します。
     * add: v1.3.0
     * @param player プレイヤー
     * @param name ホーム名
     */
    public MyHome getHome(UUID player, String name) {
        return adhome.store.getHome(player, name);
    }

    /**
     * プレイヤーのホームを設定します。
     * デフォルトホームを設定するには、nameをnullまたは空欄にします。
     * @param player プレイヤー
     * @param home ホーム名
     */
    public void setHome(Player player, MyHome home) {
        adhome.store.setHome(player, home);
    }

    /**
     * プレイヤーのホームを設定します。
     * デフォルトホームを設定するには、nameをnullまたは空欄にします。
     * add: v1.3.0
     * @param player プレイヤー
     * @param home ホーム
     */
    public void setHome(UUID player, MyHome home) {
        adhome.store.setHome(player, home);
    }

    /**
     * プレイヤーのホームを削除します。
     * デフォルトホームを削除するには、nameをnullまたは空欄にします。
     * @param player プレイヤー
     * @param home ホーム
     */
    public void deleteHome(Player player, MyHome home) {
        adhome.store.deleteHome(player, home.getName());
    }

    /**
     * プレイヤーのホームを削除します。
     * デフォルトホームを削除するには、nameをnullまたは空欄にします。
     * add: v1.3.0
     * @param player プレイヤー
     * @param home ホーム
     */
    public void deleteHome(UUID player, MyHome home) {
        adhome.store.deleteHome(player, home.getName());
    }

    /**
     * プレイヤーの全てのホームの名前を取得します。
     * @param player プレイヤー
     */
    public Set<String> getAllHomeNames(Player player) {
        return adhome.store.getAllHomeNames(player);
    }

    /**
     * プレイヤーの全てのホームの名前を取得します。
     * add: v1.3.0
     * @param player プレイヤー
     */
    public Set<String> getAllHomeNames(UUID player) {
        return adhome.store.getAllHomeNames(player);
    }

    /**
     * プレイヤーのデフォルトホームを含む全てのホームを取得します。
     * 名前を取得する場合は、getAllHomeNamesを使用してください。速度面で有利です。
     * @param player プレイヤー
     */
    public Set<MyHome> getAllHomes(Player player) {
        return new HashSet<>(adhome.store.getAllHomes(player));
    }

    /**
     * プレイヤーのデフォルトホームを含む全てのホームを取得します。
     * 名前を取得する場合は、getAllHomeNamesを使用してください。速度面で有利です。
     * add: v1.3.0
     * @param player プレイヤー
     */
    public Set<MyHome> getAllHomes(UUID player) {
        return new HashSet<>(adhome.store.getAllHomes(player));
    }

    /**
     * 全プレイヤーのUUIDを取得します
     * 読み込みに失敗したエントリは無視します
     * add: v1.3.0
     */
    public UUID[] getUsers() {
        return adhome.store.getUsers();
    }

}
