package org.ausweis;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class AusWeis extends JavaPlugin implements CommandExecutor {

    private static AusWeis instance;
    private ExecutorService apiPool;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        configManager = new ConfigManager(this);

        int poolSize = configManager.getInt("thread-pool-size", 4);
        apiPool = Executors.newFixedThreadPool(poolSize);

        getServer().getPluginManager().registerEvents(new PlayerLoginListener(this), this);

        // 注册命令
        getCommand("ausweis").setExecutor(this);

        getLogger().info("AusWeis enabled");
    }

    @Override
    public void onDisable() {
        if (apiPool != null) {
            apiPool.shutdown();
            try {
                if (!apiPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    apiPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                apiPool.shutdownNow();
            }
        }
        getLogger().info("AusWeis disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage("§cUsage: /" + label + " reload");
            return true;
        }

        if (!sender.hasPermission("ausweis.reload")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        try {
            configManager.reload();
            sender.sendMessage("§aAusWeis configuration reloaded successfully.");
            getLogger().info("Configuration reloaded by " + sender.getName());
        } catch (Exception e) {
            sender.sendMessage("§cError reloading configuration. Check console for details.");
            getLogger().severe("Error reloading config: " + e.getMessage());
        }
        return true;
    }

    public static AusWeis getInstance() {
        return instance;
    }

    public ExecutorService getApiPool() {
        return apiPool;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}