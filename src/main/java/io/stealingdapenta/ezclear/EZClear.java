package io.stealingdapenta.ezclear;

import java.util.Objects;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class EZClear extends JavaPlugin {

    private static final String PLUGIN_ENABLED = "Console clear plugin enabled.";
    private static final String PLUGIN_DISABLED = "Console clear plugin disabled.";
    private final ConsoleClearCommand consoleClearCommand = new ConsoleClearCommand();

    @Override
    public void onEnable() {
        Objects.requireNonNull(this.getCommand("consoleclear"))
               .setExecutor(consoleClearCommand);

        getLogger().info(ChatColor.GOLD + PLUGIN_ENABLED);
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.GOLD + PLUGIN_DISABLED);
    }
}