package me.syfe.stateful.listeners;

import me.syfe.stateful.Stateful;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.sql.SQLException;

public class PlayerDeathListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) throws SQLException {
        Player player = event.getEntity();
        if (Stateful.getDatabase().keepInventoryEnabled(player.getUniqueId())) {
            event.setKeepInventory(true);
            event.getDrops().clear();
        }
    }
}
