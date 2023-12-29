package io.stealingdapenta.ezclear;

import java.util.Objects;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class EZClear extends JavaPlugin {

    private static final String PLUGIN_ENABLED = "Console clear plugin enabled.";
    private static final String PLUGIN_DISABLED = "Console clear plugin disabled.";
    public static Logger logger;
    private static EZClear instance = null;
    private final ConsoleClearCommand consoleClearCommand = new ConsoleClearCommand();

    @Override
    public void onEnable() {
        instance = this;
        logger = this.getLogger();

        Objects.requireNonNull(this.getCommand("consoleclear"))
               .setExecutor(consoleClearCommand);

        logger.info(ChatColor.GOLD + PLUGIN_ENABLED);
    }

    @Override
    public void onDisable() {
        logger.info(ChatColor.GOLD + PLUGIN_DISABLED);
    }
}