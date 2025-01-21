package me.syfe.stateful.listeners.misc;

import me.syfe.stateful.Stateful;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

import java.sql.SQLException;
import java.util.Objects;

import static me.syfe.stateful.util.CustomMessage.addPrefix;

public class LeashListener implements Listener {
    @EventHandler
    public static void onLeash(PlayerLeashEntityEvent event) throws SQLException {
        if (!(Stateful.getInstance().getConfig().getBoolean("stableHorseModule"))) {
            return;
        }

        if (!(event.getEntity() instanceof AbstractHorse steed)) {
            return;
        }

        if (steed.getOwnerUniqueId() == null) {
            return;
        }

        Player player = event.getPlayer();

        if (Stateful.getInstance().getDatabase().isLocked(steed.getUniqueId().toString())
                && !(Stateful.getInstance().getDatabase().isLockedBy(steed.getUniqueId().toString(), player.getUniqueId().toString()))) {
            player.sendMessage(
                    addPrefix(
                            Component.text("You cannot leash another player's steed. This steed is owned by ")
                                    .color(NamedTextColor.RED)
                                    .append(Component.text(Objects.requireNonNull(steed.getOwner()).getName()))
                    )
            );
            event.setCancelled(true);
        }
    }

    @EventHandler
    public static void onUnleash(PlayerUnleashEntityEvent event) throws SQLException {
        if (!(Stateful.getInstance().getConfig().getBoolean("stableHorseModule"))) {
            return;
        }

        if (!(event.getEntity() instanceof AbstractHorse steed)) {
            return;
        }

        if (steed.getOwnerUniqueId() == null) {
            return;
        }

        Player player = event.getPlayer();

        if (Stateful.getInstance().getDatabase().isLocked(steed.getUniqueId().toString())
                && !(Stateful.getInstance().getDatabase().isLockedBy(steed.getUniqueId().toString(), player.getUniqueId().toString()))) {
            player.sendMessage(
                    addPrefix(
                            Component.text("You cannot unleash another player's steed. This steed is owned by ")
                                    .color(NamedTextColor.RED)
                                    .append(Component.text(Objects.requireNonNull(steed.getOwner()).getName()))
                    )
            );
            event.setCancelled(true);
        }
    }

    @EventHandler
    public static void onLeashHitchDestroyed(HangingBreakByEntityEvent event) throws SQLException {
        if (!(Stateful.getInstance().getConfig().getBoolean("stableHorseModule"))) {
            return;
        }

        if (event.getEntity() instanceof LeashHitch leashHitch && event.getRemover() instanceof Player player) {
            // loop through all entities within bounding box of leash hitch and check if they are horses leashed to our hitch
            for (Entity entity : leashHitch.getNearbyEntities(20, 20, 20)) {
                if (entity instanceof AbstractHorse steed && steed.getLeashHolder().equals(leashHitch)) {
                    if (steed.getOwnerUniqueId() == null) {
                        return;
                    }

                    if (Stateful.getInstance().getDatabase().isLocked(steed.getUniqueId().toString())
                            && !(Stateful.getInstance().getDatabase().isLockedBy(steed.getUniqueId().toString(), player.getUniqueId().toString()))) {
                        player.sendMessage(
                                addPrefix(
                                        Component.text("You cannot remove the leash from another player's steed. This steed is owned by ")
                                                .color(NamedTextColor.RED)
                                                .append(Component.text(Objects.requireNonNull(steed.getOwner()).getName()))
                                )
                        );
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}