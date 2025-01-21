package me.syfe.stateful.listeners.vehicle;

import me.syfe.stateful.Stateful;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.Listener;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import java.sql.SQLException;
import java.util.Objects;

import static me.syfe.stateful.util.CustomMessage.addPrefix;

public class VehicleEnterListener implements Listener {
    @EventHandler
    public static void onEntityMount(VehicleEnterEvent event) throws SQLException {
        if (event.getVehicle() instanceof AbstractHorse steed && event.getEntered() instanceof Player player) {
            if (steed.getOwnerUniqueId() == null) {
                return;
            }

            if (Stateful.getInstance().getDatabase().isLocked(steed.getUniqueId().toString())
                    && !(Stateful.getInstance().getDatabase().isLockedBy(steed.getUniqueId().toString(), player.getUniqueId().toString()))) {
                player.sendMessage(
                        addPrefix(
                                Component.text("You cannot ride another player's locked steed. This steed is owned by ")
                                        .color(NamedTextColor.RED)
                                        .append(Component.text(Objects.requireNonNull(steed.getOwner()).getName()))
                        )
                );
                event.setCancelled(true);
            }
        }
    }
}