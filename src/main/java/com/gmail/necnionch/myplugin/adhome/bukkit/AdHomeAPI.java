package com.gmail.necnionch.myplugin.adhome.bukkit;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("ALL")
public class AdHomeAPI {
    private AdHomePlugin adhome;

    public AdHomeAPI(AdHomePlugin adhome) {
        this.adhome = adhome;
    }

    public String getWorldDisplay(String worldName) {
        /**
         * 指定されたワールド名に表示名が設定されていれば、その文字列に置換します。
         * @param worldName ワールド名
         */
        return adhome.config.getWorldDisplay(worldName);
    }

    public MyHome getHome(Player player, String name) {
        /**
         * プレイヤーのホームを取得します。
         * デフォルトホームを取得するには、nameをnullまたは空欄にします。
         * 存在しない場合は null を返します。
         * @param player プレイヤー
         * @param name ホーム名
         */
        return adhome.store.getHome(player, name);
    }

    public MyHome getHome(UUID player, String name) {
        /**
         * プレイヤーのホームを取得します。
         * デフォルトホームを取得するには、nameをnullまたは空欄にします。
         * 存在しない場合は null を返します。
         * add: v1.3.0
         * @param player プレイヤー
         * @param name ホーム名
         */
        return adhome.store.getHome(player, name);
    }

    public void setHome(Player player, MyHome home) {
        /**
         * プレイヤーのホームを設定します。
         * デフォルトホームを設定するには、nameをnullまたは空欄にします。
         * @param player プレイヤー
         * @param name ホーム名
         */
        adhome.store.setHome(player, home);
    }

    public void setHome(UUID player, MyHome home) {
        /**
         * プレイヤーのホームを設定します。
         * デフォルトホームを設定するには、nameをnullまたは空欄にします。
         * add: v1.3.0
         * @param player プレイヤー
         * @param name ホーム名
         */
        adhome.store.setHome(player, home);
    }

    public void deleteHome(Player player, MyHome home) {
        /**
         * プレイヤーのホームを削除します。
         * デフォルトホームを削除するには、nameをnullまたは空欄にします。
         * @param player プレイヤー
         * @param name ホーム名
         */
        adhome.store.deleteHome(player, home.getName());
    }

    public void deleteHome(UUID player, MyHome home) {
        /**
         * プレイヤーのホームを削除します。
         * デフォルトホームを削除するには、nameをnullまたは空欄にします。
         * add: v1.3.0
         * @param player プレイヤー
         * @param name ホーム名
         */
        adhome.store.deleteHome(player, home.getName());
    }

    public Set<String> getAllHomeNames(Player player) {
        /**
         * プレイヤーの全てのホームの名前を取得します。
         * @param player プレイヤー
         */
        return adhome.store.getAllHomeNames(player);
    }

    public Set<String> getAllHomeNames(UUID player) {
        /**
         * プレイヤーの全てのホームの名前を取得します。
         * add: v1.3.0
         * @param player プレイヤー
         */
        return adhome.store.getAllHomeNames(player);
    }

    public Set<MyHome> getAllHomes(Player player) {
        /**
         * プレイヤーの全てのホームを取得します。
         * 名前を取得する場合は、getAllHomeNamesを使用してください。速度面で有利です。
         * @param player プレイヤー
         */
        return new HashSet<>(adhome.store.getAllHomes(player));
    }

    public Set<MyHome> getAllHomes(UUID player) {
        /**
         * プレイヤーの全てのホームを取得します。
         * 名前を取得する場合は、getAllHomeNamesを使用してください。速度面で有利です。
         * add: v1.3.0
         * @param player プレイヤー
         */
        return new HashSet<>(adhome.store.getAllHomes(player));
    }

    public UUID[] getUsers() {
        /**
         * 全プレイヤーのUUIDを取得します
         * 読み込みに失敗したエントリは無視します
         * add: v1.3.0
         */
        return adhome.store.getUsers();
    }

}
