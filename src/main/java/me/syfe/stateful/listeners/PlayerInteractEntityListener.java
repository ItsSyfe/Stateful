package me.syfe.stateful.listeners;

import me.syfe.stateful.Stateful;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntityListener implements Listener {
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (Stateful.getInstance().getConfig().getBoolean("invisibleItemFramesModule")
                && event.getPlayer().isSneaking()
                && event.getRightClicked() instanceof ItemFrame frame
                && !frame.getItem().isEmpty()) {
            frame.setInvisible(!frame.isInvisible());
            frame.setFixed(!frame.isFixed());
            event.setCancelled(true);
        }
    }
}
