package org.ausweis;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class LangManager {

    private final JavaPlugin plugin;
    private final ConfigManager cfg;
    private final Map<String, YamlConfiguration> languages = new HashMap<>();
    private YamlConfiguration currentLang;

    public LangManager(JavaPlugin plugin, ConfigManager cfg) {
        this.plugin = plugin;
        this.cfg = cfg;
        loadLanguages();
        loadCurrentLanguage();
    }

    private void loadLanguages() {
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            if (!langFolder.mkdirs()) {
                plugin.getLogger().warning("Failed to create language folder: " + langFolder.getPath());
            }
            saveDefaultLanguageFiles();
        }

        File[] files = langFolder.listFiles((dir, name) -> name.startsWith("messages_") && name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String langCode = file.getName().replace("messages_", "").replace(".yml", "");
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                languages.put(langCode, config);
                plugin.getLogger().info("Loaded language: " + langCode);
            }
        } else {
            plugin.getLogger().warning("No language files found in " + langFolder.getPath());
        }

        if (!languages.containsKey("en")) {
            languages.put("en", new YamlConfiguration());
        }
    }

    private void loadCurrentLanguage() {
        String lang = cfg.getLanguage();
        currentLang = languages.get(lang);
        if (currentLang == null) {
            plugin.getLogger().warning("Language '" + lang + "' not found, falling back to 'en'.");
            currentLang = languages.get("en");
            if (currentLang == null) {
                currentLang = new YamlConfiguration();
            }
        }
    }

    private void saveDefaultLanguageFiles() {
        String[] defaultFiles = {"messages_en.yml", "messages_zh.yml"};
        for (String fileName : defaultFiles) {
            File target = new File(plugin.getDataFolder() + "/lang", fileName);
            if (!target.exists()) {
                try (InputStream in = plugin.getResource("lang/" + fileName)) {
                    if (in != null) {
                        Files.copy(in, target.toPath());
                    } else {
                        plugin.getLogger().warning("Could not find built-in language file: " + fileName);
                    }
                } catch (IOException e) {
                    plugin.getLogger().log(Level.WARNING, "Could not save language file: " + fileName, e);
                }
            }
        }
    }

    public String getMessage(String key) {
        return currentLang.getString(key, "");
    }

    public String getMessage(String key, Map<String, String> placeholders) {
        String msg = getMessage(key);
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                msg = msg.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return msg;
    }

    public void reload() {
        languages.clear();
        loadLanguages();
        loadCurrentLanguage();
    }
}