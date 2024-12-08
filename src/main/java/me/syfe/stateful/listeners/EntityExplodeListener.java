package me.syfe.stateful.listeners;

import me.syfe.stateful.Stateful;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodeListener implements Listener {
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (Stateful.getInstance().getConfig().getBoolean("antiCreeperGriefModule") && event.getEntityType() == EntityType.CREEPER) {
            event.setCancelled(true);
            event.getLocation().getWorld().playSound(event.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.56f + Stateful.getRandom().nextFloat() * (0.84f - 0.56f));
        }
    }
}
