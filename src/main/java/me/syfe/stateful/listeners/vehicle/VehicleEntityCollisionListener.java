package me.syfe.stateful.listeners.vehicle;

import me.syfe.stateful.Stateful;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

import java.sql.SQLException;

public class VehicleEntityCollisionListener implements Listener {
    @EventHandler
    public static void onCollision(VehicleEntityCollisionEvent event) throws SQLException {
        if (event.getVehicle() instanceof AbstractHorse steed && event.getEntity() instanceof Player player) {
            if (steed.getOwnerUniqueId() == null) {
                return;
            }

            if (Stateful.getInstance().getDatabase().isLocked(steed.getUniqueId().toString())
                    && !(Stateful.getInstance().getDatabase().isLockedBy(steed.getUniqueId().toString(), player.getUniqueId().toString()))) {
                event.setCancelled(true);
            }
        }
    }
}
