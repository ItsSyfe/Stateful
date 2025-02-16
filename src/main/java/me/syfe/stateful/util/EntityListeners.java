package me.syfe.stateful.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class EntityListeners implements Listener {
    DespawnImmunityManager despawnManager;

    ArrayList<Integer> portalInteractionEntities = new ArrayList<>();

    public EntityListeners(DespawnImmunityManager despawnManager) {
        this.despawnManager = despawnManager;
    }

    public void clearPortalEntities() {
        this.portalInteractionEntities.clear();
    }

    @EventHandler
    public void on(EntityPortalEnterEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            if (livingEntity.getRemoveWhenFarAway()) {
                if (this.portalInteractionEntities.contains(Integer.valueOf(livingEntity.getEntityId())))
                    return;
                livingEntity.getScheduler().execute((Plugin)this.despawnManager.plugin, () -> {
                    this.despawnManager.setImmuneToHardDespawn(livingEntity, true);
                    this.despawnManager.enqueueImmunityRemoval(livingEntity);
                    this.portalInteractionEntities.add(Integer.valueOf(livingEntity.getEntityId()));
                },null, 1L);
            }
        }
    }

    @EventHandler
    public void on(EntitiesLoadEvent event) {
        for (Entity entity : event.getEntities()) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                if (!livingEntity.getRemoveWhenFarAway())
                    this.despawnManager.removeDespawnImmunityIfExpired(livingEntity);
            }
        }
    }
}