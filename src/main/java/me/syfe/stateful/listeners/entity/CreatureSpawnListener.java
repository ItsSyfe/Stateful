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

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import me.syfe.stateful.Stateful;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.*;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Map;

public class CreatureSpawnListener implements Listener {
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if(Stateful.getInstance().getConfig().getBoolean("husksReplacedPyramidModule")) {
            handleHuskSpawningInPyramids(event);
        }
    }

    private void handleHuskSpawningInPyramids(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
            return;
        }

        if (event.getEntity() instanceof Monster monster && isInsideDesertPyramid(monster) && monster.getType() != EntityType.HUSK) {
            event.setCancelled(true);
            EntityType type = event.getEntityType();
            if (type == EntityType.ZOMBIE || type == EntityType.SKELETON || type == EntityType.CREEPER) {
                Location l = monster.getLocation();
                l.getWorld().spawnEntity(l, EntityType.HUSK);
            }
        }
    }

    private static int[] getChunkCoords(long chunkKey) {
        int x = (int)chunkKey;
        int z = (int)(chunkKey >> 32L);
        return new int[] { x, z };
    }

    private static boolean isInsideDesertPyramid(LivingEntity entity) {
        Location location = entity.getLocation();
        Chunk entityChunk = location.getChunk();
        ServerLevel level = ((CraftWorld)entity.getWorld()).getHandle();
        StructureManager manager = level.structureManager();
        Structure structure = (Structure)((Registry)manager.registryAccess().lookupOrThrow(Registries.STRUCTURE)).getValue(BuiltinStructures.DESERT_PYRAMID);
        if (structure != null) {
            LongSet set = level.getChunk(entityChunk.getX(), entityChunk.getZ()).getReferencesForStructure(structure);
            if (!set.isEmpty()) {
                LongIterator iterator = set.iterator();
                while (iterator.hasNext()) {
                    long possibleStartKey = iterator.nextLong();
                    int[] coords = getChunkCoords(possibleStartKey);
                    Map<Structure, StructureStart> structureMap = level.getChunk(coords[0], coords[1]).getAllStarts();
                    StructureStart start = structureMap.get(structure);
                    if (start == null)
                        continue;
                    BoundingBox box = start.getBoundingBox();
                    Vec3i entityPos = new Vec3i(location.getBlockX(), location.getBlockY(), location.getBlockZ());
                    if (!box.isInside(entityPos))
                        continue;
                    for (StructurePiece piece : start.getPieces()) {
                        if (piece.getBoundingBox().isInside(entityPos))
                            return true;
                    }
                }
            }
        }
        return false;
    }
}
