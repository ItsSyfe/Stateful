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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.structures.DesertPyramidStructure;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.StructureSearchResult;
import org.bukkit.util.Vector;

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

            StructureSearchResult desertTempleSearchResult = world.locateNearestStructure(mobSpawnLocation, StructureType.DESERT_PYRAMID, 30, false);
            if(desertTempleSearchResult == null){
                return;     // Guard method for when mob is not near desert temple
            }

            Location desertTempleLocation = desertTempleSearchResult.getLocation();

            Chunk chunk = world.getChunkAt(desertTempleLocation);

            // fuck nms
            ServerLevel nmsWorld = ((CraftWorld) chunk.getWorld()).getHandle();
            StructureManager nmsStructureManager = nmsWorld.structureManager();
            ChunkPos nmsChunk = new ChunkPos(chunk.getX(), chunk.getZ());

            for (StructureStart structureStart : nmsStructureManager.startsForStructure(nmsChunk, (nmsStructure) -> nmsStructure instanceof DesertPyramidStructure)) {
                net.minecraft.world.level.levelgen.structure.BoundingBox nmsBoundingBox = structureStart.getBoundingBox();
                BoundingBox boundingBox = BoundingBox.of(
                        new Vector(nmsBoundingBox.minX(), nmsBoundingBox.minY(), nmsBoundingBox.minZ()),
                        new Vector(nmsBoundingBox.maxX(), nmsBoundingBox.maxY(), nmsBoundingBox.maxZ())
                );

                if(boundingBox.contains(mobSpawnLocation.toVector())){
                    event.setCancelled(true);
                    world.spawnEntity(mobSpawnLocation, EntityType.HUSK);
                    return;
                }
            }
            // ^ nms
        }
    }
}
