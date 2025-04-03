 package me.syfe.stateful.listeners.misc;

import me.syfe.stateful.Stateful;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BlockPlaceListener implements Listener {
    NamespacedKey preventPlacementKey = new NamespacedKey(Stateful.getInstance(), "prevent_placement");

    @SuppressWarnings("DataFlowIssue")
    @EventHandler
    public void preventBlockPlace(BlockPlaceEvent event) {
        if (Stateful.getInstance().getConfig().getBoolean("customAttributeModule")) {
            ItemStack stack = event.getPlayer().getInventory().getItem(event.getHand());
            ItemMeta meta = stack.getItemMeta();
            if (meta == null) return;
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.has(preventPlacementKey, PersistentDataType.BOOLEAN) && container.get(preventPlacementKey, PersistentDataType.BOOLEAN)) {
                event.setCancelled(true);
            }
        }
    }
}
