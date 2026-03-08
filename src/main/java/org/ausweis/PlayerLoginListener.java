package org.ausweis;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PlayerLoginListener implements Listener {

    private final AusWeis plugin;
    private final ConfigManager cfg;

    public PlayerLoginListener(AusWeis plugin) {
        this.plugin = plugin;
        this.cfg = plugin.getConfigManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        boolean verified = checkApi(playerName);
        if (!verified) {
            String verifyUrl = cfg.getVerifyUrl().replace("{player}", encode(playerName));
            String kickMessage = cfg.getKickMessage().replace("{verify-url}", verifyUrl);
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, kickMessage);
            if (cfg.isDebug()) {
                plugin.getLogger().info("Kicked unverified player " + playerName + " with URL: " + verifyUrl);
            }
        } else {
            plugin.getLogger().fine("Player " + playerName + " is verified.");
        }
    }

    private boolean checkApi(String playerName) {
        try {
            String apiUrl = cfg.getApiUrl().replace("{player}", encode(playerName));
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(cfg.getTimeout());
            conn.setReadTimeout(cfg.getTimeout());
            conn.setRequestProperty("User-Agent", "AusWeis/1.0");
            conn.setInstanceFollowRedirects(true);

            int code = conn.getResponseCode();
            if (code == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                    return parseVerificationResponse(sb.toString());
                }
            } else {
                plugin.getLogger().warning("API request failed with HTTP " + code + ": " + apiUrl);
                return false;
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "API request error: " + e.getMessage(), e);
            return false;
        }
    }

    private boolean parseVerificationResponse(String json) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            return obj.get("verified").getAsBoolean();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to parse API response: " + json);
            return false;
        }
    }

    private String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}