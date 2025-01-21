package me.syfe.stateful.listeners.entity;

import me.syfe.stateful.Stateful;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

import java.sql.SQLException;
import java.util.Objects;

import static me.syfe.stateful.util.CustomMessage.addPrefix;

public class EntityBreedListener implements Listener {
    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) throws SQLException {
        if (Stateful.getInstance().getConfig().getBoolean("stableHorseModule")) {
            handleStableHorse(event);
        }
    }

    private void handleStableHorse(EntityBreedEvent event) throws SQLException {
        if (event.getFather() instanceof AbstractHorse father && event.getMother() instanceof AbstractHorse mother && event.getBreeder() instanceof Player player) {
            if (father.getOwnerUniqueId() != null && mother.getOwnerUniqueId() != null) {
                return;
            }

            if ((Stateful.getInstance().getDatabase().isLocked(father.getUniqueId().toString())
                    && !Stateful.getInstance().getDatabase().isLockedBy(father.getUniqueId().toString(), player.getUniqueId().toString()))
                    || (Stateful.getInstance().getDatabase().isLocked(father.getUniqueId().toString())
                    && !Stateful.getInstance().getDatabase().isLockedBy(mother.getUniqueId().toString(), player.getUniqueId().toString()))) {
                player.sendMessage(
                        addPrefix(
                                Component.text("You cannot breed another player's locked steed. This steed is owned by ")
                                        .color(NamedTextColor.RED)
                                        .append(Component.text((father.getOwnerUniqueId() != null ? Objects.requireNonNull(father.getOwner()).getName() : Objects.requireNonNull(mother.getOwner()).getName())))
                        )
                );
                event.setCancelled(true);
            }
        }
    }
}
