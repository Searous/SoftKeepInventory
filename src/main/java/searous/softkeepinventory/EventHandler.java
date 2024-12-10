package searous.softkeepinventory;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.logging.Level;

public class EventHandler implements Listener {
    private final SoftKeepInventory plugin;

    public EventHandler(SoftKeepInventory plugin) {
        this.plugin = plugin;
    }

    @org.bukkit.event.EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();

        // Skip this death if not in an enabled world when world specific mode is enabled
        if(plugin.enableOnlyInWorld && !plugin.inWorld.contains(player.getWorld().getName()))
            return;

        // Determine keep mode; defaults to global keep mode, but uses pvp if a player hit us last
        KeepMode keepMode = plugin.keepMode;
        if(player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) player.getLastDamageCause();
            if(damageEvent.getDamager() instanceof Player)
                keepMode = plugin.pvpKeepMode;
        }

        // Set keep level
        e.setKeepLevel(plugin.getConfig().getBoolean("keepXP"));

        // Use our keep mode to determine which drop mechanics to use for this death
        plugin.getLogger().log(Level.INFO, "Handling death using " + keepMode.toString());
        switch(keepMode) {
            case NONE: { // Default drop mechanics; not needed, but left here if we need custom logic in the future
                break;
            }
            case ALL: { // Default keep inventory drop mechanics
                e.setKeepInventory(true);
                break;
            }
            case SOME: { // Use our custom drop logic
                doCustomDropLogic(e);
                break;
            }
        }
    }

    private void doCustomDropLogic(PlayerDeathEvent e) {
        // Set keep inventory and clear drops list to prevent dupes and allow us to handle dropping items
        e.setKeepInventory(true);
        e.getDrops().clear();

        // Iterate over player's inventory and drop items that do not meet the criteria
        PlayerInventory inv = e.getEntity().getInventory();
        for(int i = 0; i < inv.getSize(); i++) {
            // Fetch stack from inventory
            ItemStack stack = inv.getItem(i);
            if(stack == null)
                continue;

            // Skip if this slot shouldn't be dropped
            if(plugin.keepSlots.contains(i))
                continue;;

            // Skip if this item type shouldn't be dropped
            if(plugin.keepItems.contains(stack.getType().getKey().toString()))
                continue;

            plugin.getLogger().log(Level.INFO, "Dropping item: " + stack.getType().getKey());

            // Remove the item from the player's inventory and re-add it to the drops list
            inv.clear(i);
            e.getDrops().add(stack);
        }
    }
}

