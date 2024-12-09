package me.syfe.stateful.listeners.player;

import me.syfe.stateful.Stateful;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.SQLException;

public class PlayerDeathListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) throws SQLException {
        Player player = event.getEntity();

        // Keep Inventory Logic - START
        if (Stateful.getInstance().getConfig().getBoolean("keepInventoryModule")
                && Stateful.getInstance().getDatabase().keepInventoryEnabled(player.getUniqueId())) {
            event.setKeepInventory(true);
            event.getDrops().clear();
            event.deathMessage(
                event.deathMessage()
                    .append(Component.text()
                            .color(TextColor.color(0xFF0000))
                            .content(" ❤")
                            .build()
                    )
            );
        }
        // Keep Inventory Logic - END

        // Player Head Drops - START
        if (Stateful.getInstance().getConfig().getBoolean("playerHeadDropsModule")) {
            if (player == player.getKiller()
                    && player.getLastDamageCause() instanceof EntityDamageByEntityEvent damageEvent
                    && damageEvent.getCause() == EntityDamageByEntityEvent.DamageCause.PROJECTILE) {
                ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta meta = (SkullMeta) stack.getItemMeta();
                meta.setOwningPlayer(player);
                stack.setItemMeta(meta);
                player.getWorld().dropItemNaturally(player.getLocation(), stack);
                event.deathMessage(null);
            }
        }
        // Player Head Drops - END
    }
}
