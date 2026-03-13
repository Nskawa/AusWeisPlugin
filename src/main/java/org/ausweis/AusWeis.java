package org.ausweis;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class AusWeis extends JavaPlugin implements CommandExecutor {

    private ConfigManager configManager;
    private LangManager langManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        langManager = new LangManager(this, configManager);

        getServer().getPluginManager().registerEvents(new PlayerLoginListener(this, configManager, langManager), this);

        PluginCommand ausweisCommand = getCommand("ausweis");
        if (ausweisCommand != null) {
            ausweisCommand.setExecutor(this);
        }

        getLogger().info("AusWeis enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("AusWeis disabled");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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
            langManager.reload();
            sender.sendMessage("§aAusWeis configuration reloaded successfully.");
            getLogger().info("Configuration reloaded by " + sender.getName());
        } catch (Exception e) {
            sender.sendMessage("§cError reloading configuration. Check console for details.");
            getLogger().severe("Error reloading config: " + e.getMessage());
        }
        return true;
    }
}