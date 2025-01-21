/**
 * Stateful, Minecraft Plugin for the FlowSMP
 * Copyright (C) 2024  Syfe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package me.syfe.stateful.listeners.player;

import me.syfe.stateful.Stateful;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.sql.SQLException;
import java.util.Objects;

import static me.syfe.stateful.util.CustomMessage.addPrefix;

public class PlayerInteractEntityListener implements Listener {
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) throws SQLException {
        if (Stateful.getInstance().getConfig().getBoolean("invisibleItemFramesModule")
                && event.getPlayer().isSneaking()
                && event.getRightClicked() instanceof ItemFrame frame
                && !frame.getItem().isEmpty()) {
            frame.setInvisible(!frame.isInvisible());
            frame.setFixed(!frame.isFixed());
            event.setCancelled(true);
        }

        if (Stateful.getInstance().getConfig().getBoolean("stableHorseModule")) {
            handleStableHorse(event);
        }
    }

    private static void handleStableHorse(PlayerInteractEntityEvent event) throws SQLException {
        if (event.getRightClicked() instanceof AbstractHorse steed) {
            if (steed.getOwnerUniqueId() == null) {
                return;
            }

            Player player = event.getPlayer();

            if (Stateful.getInstance().getDatabase().isLocked(steed.getUniqueId().toString())
                    && !(Stateful.getInstance().getDatabase().isLockedBy(steed.getUniqueId().toString(), player.getUniqueId().toString()))) {
                player.sendMessage(
                        addPrefix(
                                Component.text("You cannot access/ride another player's locked steed.  This steed is owned by ")
                                        .color(NamedTextColor.RED)
                                        .append(Component.text(Objects.requireNonNull(Objects.requireNonNull(steed.getOwner()).getName())))
                        )
                );
                event.setCancelled(true);
            }
        }
    }
}
