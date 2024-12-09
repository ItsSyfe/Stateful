package me.syfe.stateful.listeners.player;

import me.syfe.stateful.Stateful;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws SQLException {
        // Keep Inventory Logic - START
        if (Stateful.getInstance().getConfig().getBoolean("keepInventoryModule")
                && !Stateful.getInstance().getDatabase().keepInventoryPlayerExists(event.getPlayer().getUniqueId())) {
            Stateful.getInstance().getDatabase().keepInventoryInsert(event.getPlayer().getUniqueId());

            // both messages here because im lazy to add another detection for new joins
            event.getPlayer().sendMessage(
                    Component.text("Welcome to the ")
                            .append(Component.text("FlowSMP", TextColor.color(0x00DEE6)))
                            .append(Component.text(", we hope you enjoy playing and hanging out with us! If you have any questions, feel free to ask in chat!"))
            );

            event.getPlayer().sendMessage(Component.text("As one of our features your inventory will be kept on death! If you'd like to disable this feature, use /keepinventory to toggle it!"));
        }
        // Keep Inventory Logic - END
    }
}
