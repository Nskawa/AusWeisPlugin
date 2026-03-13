package org.ausweis;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    private static final String LANGUAGE = "language";
    private static final String API_URL = "api-url";
    private static final String VERIFY_URL = "verify-url";
    private static final String TIMEOUT = "timeout";
    private static final String DEBUG = "debug";

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public String getLanguage() {
        return config.getString(LANGUAGE, "en");
    }

    public String getApiUrl() {
        return config.getString(API_URL, "https://ausweis.lya.bz/api/check?user={player}");
    }

    public String getVerifyUrl() {
        return config.getString(VERIFY_URL, "https://ausweis.lya.bz?user={player}");
    }

    public int getTimeout() {
        return config.getInt(TIMEOUT, 5000);
    }

    public boolean isDebug() {
        return config.getBoolean(DEBUG, false);
    }
}