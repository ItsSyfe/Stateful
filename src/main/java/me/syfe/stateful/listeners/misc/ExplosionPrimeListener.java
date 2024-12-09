package me.syfe.stateful.listeners.misc;

import me.syfe.stateful.Stateful;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class ExplosionPrimeListener implements Listener {
    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (Stateful.getInstance().getConfig().getBoolean("antiEndermanGriefModule") && event.getEntityType() == EntityType.FIREBALL) {
            event.setCancelled(true);
        }
    }
}
