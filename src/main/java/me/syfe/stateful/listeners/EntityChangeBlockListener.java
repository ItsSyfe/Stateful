package me.syfe.stateful.listeners;

import me.syfe.stateful.Stateful;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EntityChangeBlockListener implements Listener {
    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (Stateful.getInstance().getConfig().getBoolean("antiEndermanGriefModule") && event.getEntityType() == EntityType.ENDERMAN) {
            event.setCancelled(true);
        }
    }
}
