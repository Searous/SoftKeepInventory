package searous.softkeepinventory;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class SoftKeepInventory extends JavaPlugin {

    private FileConfiguration mainConfig;
    public FileConfiguration getMainConfig() { return mainConfig; }

    private EventHandler eventHandler;

    public boolean enableOnlyInWorld;
    public List<String> inWorld;
    public List<String> keepItems;
    public List<Integer> keepSlots;
    public KeepMode keepMode;
    public KeepMode pvpKeepMode;

    @Override
    public void onEnable() {
        // Create event handler
        eventHandler = new EventHandler(this);

        // Register events
        getServer().getPluginManager().registerEvents(eventHandler, this);

        // Load config
        this.saveDefaultConfig();
        this.loadConfig();

        // Commands
        this.getCommand("softkeepinventory").setExecutor(new CommandRelaod(this));

        // Plugin startup logic
        getLogger().log(Level.INFO, "Plugin loaded!");
    }

    private void loadConfig() {
        mainConfig = getConfig();

        // Please future me, refactor this if we go above 10 lines we need to load here
        keepItems = mainConfig.getStringList("keepItems");
        inWorld = mainConfig.getStringList("worlds");
        enableOnlyInWorld = mainConfig.getBoolean("onlyInSpecificWorlds");
        keepSlots = mainConfig.getIntegerList("keepSlots");
        keepMode = KeepMode.valueOf(mainConfig.getString("mode").toUpperCase());
        pvpKeepMode = KeepMode.valueOf(mainConfig.getString("pvpMode").toUpperCase());
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        this.loadConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
