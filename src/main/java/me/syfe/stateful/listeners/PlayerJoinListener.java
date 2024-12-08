package me.syfe.stateful.listeners;

import me.syfe.stateful.Stateful;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws SQLException {
        // Keep Inventory Logic - START
        if (!Stateful.getDatabase().keepInventoryPlayerExists(event.getPlayer().getUniqueId())) {
            Stateful.getDatabase().keepInventoryInsert(event.getPlayer().getUniqueId());
        }
        // Keep Inventory Logic - END
    }
}
