package me.syfe.stateful.listeners.player;

import me.syfe.stateful.Stateful;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (Stateful.getInstance().getConfig().getBoolean("noCropTrampleModule") && event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.FARMLAND && event.getBlockFace() == BlockFace.SELF)
            event.setCancelled(true);
    }
}
