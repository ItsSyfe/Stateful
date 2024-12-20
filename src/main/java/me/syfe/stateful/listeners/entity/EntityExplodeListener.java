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

package me.syfe.stateful.listeners.entity;

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

        if (Stateful.getInstance().getConfig().getBoolean("antiGhastGriefModule") && event.getEntityType() == EntityType.FIREBALL) {
            event.setCancelled(true);
        }
    }
}
