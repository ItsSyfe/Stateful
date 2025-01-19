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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.generator.structure.StructureType;

public class CreatureSpawnListener implements Listener {
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if(Stateful.getInstance().getConfig().getBoolean("husksReplacedPyramidModule")) {
            handleHuskSpawningInPyramids(event);
        }
    }

    /**
     * Whenever a hostile mob is spawned, this method (of the module is enabled), replaces any
     * hostile mob spawned within the bounding box of a desert pyramid with a husk.
     * @param event Mob spawn event
     */
    private void handleHuskSpawningInPyramids(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL
                && event.getEntity() instanceof Monster monster) {
            World world = monster.getWorld();
            if(world.getEnvironment() != World.Environment.NORMAL){
                return;     // If mob is not in overworld, we dont care about it
            }
            Location mobSpawnLocation = monster.getLocation();
            if(world.locateNearestStructure(mobSpawnLocation, StructureType.DESERT_PYRAMID, 30, false) == null){
                return;     // Guard method for when mob is not near desert temple
            }
            Location desertTempleLocation = world.locateNearestStructure(mobSpawnLocation, StructureType.DESERT_PYRAMID, 30, false).getLocation();
            // Hard code the radius of a desert temple (centered around the checkerboard floor)
            if(isWithinBoundingBox(mobSpawnLocation, desertTempleLocation, 10)){
                event.setCancelled(true);
                world.spawnEntity(mobSpawnLocation, EntityType.HUSK);
            }
        }
    }

    /**
     * This determines if an entity is within the "bounding box" of a structure. There is no bukkit method for determining
     * a structures bounding box, and to avoid iterating through thousands of blocks to determine that, it just
     * checks to see if the entity location is within a block radius.
     * @param entityLocation Location of entity to be checked
     * @param structureLocation Location of target structure
     * @param roughStructureRadius Integer of rough block radius of structure
     * @return Returns whether the entity is within the valid dimensions of the structure
     */
    private boolean isWithinBoundingBox(Location entityLocation, Location structureLocation, Integer roughStructureRadius) {
        int minX = structureLocation.getBlockX() - roughStructureRadius;
        int maxX = structureLocation.getBlockX() + roughStructureRadius;
        int minZ = structureLocation.getBlockZ() - roughStructureRadius;
        int maxZ = structureLocation.getBlockZ() + roughStructureRadius;
        int minY = structureLocation.getBlockY() - roughStructureRadius;
        int maxY = structureLocation.getBlockY() + roughStructureRadius;

        int entityX = entityLocation.getBlockX();
        int entityZ = entityLocation.getBlockZ();
        int entityY = entityLocation.getBlockY();

        return entityX >= minX && entityX <= maxX && entityZ >= minZ && entityZ <= maxZ && entityY >= minY && entityY <= maxY;
    }
}
