package me.syfe.stateful.listeners.entity;

import me.syfe.stateful.Stateful;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDeathListener implements Listener {
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (Stateful.getInstance().getConfig().getBoolean("huskDropsSandModule")
                && event.getEntity().getType() == EntityType.HUSK) {
            int rNum = Stateful.getRandom().nextInt(1, 3);
            event.getDrops().add(new ItemStack(Material.SAND, rNum));
        }
    }
}
