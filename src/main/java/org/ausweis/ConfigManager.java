package org.ausweis;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    private static final String API_URL = "api-url";
    private static final String VERIFY_URL = "verify-url";
    private static final String TIMEOUT = "timeout";
    private static final String DEBUG = "debug";
    private static final String KICK_MESSAGE = "kick-message";
    private static final String THREAD_POOL_SIZE = "thread-pool-size";

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public String getApiUrl() {
        return config.getString(API_URL, "http://ausweis.lya.bz/api/check?user={player}");
    }

    public String getVerifyUrl() {
        return config.getString(VERIFY_URL, "http://ausweis.lya.bz?user={player}");
    }

    public int getTimeout() {
        return config.getInt(TIMEOUT, 5000);
    }

    public boolean isDebug() {
        return config.getBoolean(DEBUG, false);
    }

    public String getKickMessage() {
        return config.getString(KICK_MESSAGE, "§c⛔ Verification Required\n§7Please visit: §f{verify-url}\n§7Rejoin after verification");
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }
}