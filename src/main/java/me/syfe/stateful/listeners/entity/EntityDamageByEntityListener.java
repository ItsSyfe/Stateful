package me.syfe.stateful.listeners.entity;

import me.syfe.stateful.Stateful;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityListener implements Listener {
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!Stateful.getInstance().getConfig().getBoolean("snowballFightModule")) return;

        EntityType type = event.getDamager().getType();
        switch (type) {
            case SNOWBALL:
            case EGG:
                if (event.getDamage() == 0.0D)
                    event.setDamage(1.0E-4D);
                break;
        }
    }
}
