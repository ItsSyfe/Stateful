package me.syfe.stateful.util;

import me.syfe.stateful.Stateful;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class DespawnImmunityManager {
    private final NamespacedKey EXEMPTION_KEY;

    private final Queue<DespawnImmunityRemovalTask> exemptionRemovalTasks = new LinkedList<>();

    final Stateful plugin;

    public DespawnImmunityManager(Stateful plugin) {
        this.EXEMPTION_KEY = new NamespacedKey((Plugin)plugin, "hard-despawn-exempt-since");
        this.plugin = plugin;
    }

    public void setImmuneToHardDespawn(LivingEntity entity, boolean immune) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        if (immune) {
            long unixEpoch = System.currentTimeMillis() / 1000L;
            pdc.set(this.EXEMPTION_KEY, PersistentDataType.LONG, Long.valueOf(unixEpoch));
            entity.setRemoveWhenFarAway(false);
        } else if (pdc.has(this.EXEMPTION_KEY, PersistentDataType.LONG)) {
            pdc.remove(this.EXEMPTION_KEY);
            entity.setRemoveWhenFarAway(true);
        }
    }

    public Optional<Instant> getHardDespawnImmuneSinceTimestamp(Entity entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        if (!pdc.has(this.EXEMPTION_KEY, PersistentDataType.LONG))
            return Optional.empty();
        long unixEpoch = ((Long)pdc.get(this.EXEMPTION_KEY, PersistentDataType.LONG)).longValue();
        return Optional.of(Instant.ofEpochSecond(unixEpoch));
    }

    public void processPendingImmunityRemovalTasks() {
        DespawnImmunityRemovalTask task;
        while ((task = this.exemptionRemovalTasks.peek()) != null && task.isDue()) {
            DespawnImmunityRemovalTask finalTask = task;
            task.getEntity().getScheduler().execute((Plugin)this.plugin, () -> setImmuneToHardDespawn(finalTask.getEntity(), false), null, 1L);
            this.exemptionRemovalTasks.poll();
        }
    }

    public void removeDespawnImmunityIfExpired(LivingEntity entity) {
        getHardDespawnImmuneSinceTimestamp((Entity)entity).ifPresent(timestamp -> {
            long secondsElapsed = Duration.between(timestamp, Instant.now()).getSeconds();
            if (secondsElapsed >= this.plugin.getDespawnSeconds(entity.getType()))
                setImmuneToHardDespawn(entity, false);
        });
    }

    public void enqueueImmunityRemoval(LivingEntity entity) {
        Instant removalTimestamp = Instant.now().plusSeconds(this.plugin.getDespawnSeconds(entity.getType()));
        this.exemptionRemovalTasks.add(new DespawnImmunityRemovalTask(entity, removalTimestamp));
    }
}