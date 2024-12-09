package me.syfe.stateful.listeners;

import io.papermc.paper.event.player.PlayerTradeEvent;
import me.syfe.stateful.Stateful;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class PlayerTradeListener implements Listener {
    @EventHandler
    public void onPlayerTrade(PlayerTradeEvent event) {
        if (Stateful.getInstance().getConfig().getBoolean("noMendingTradeModule")
                && event.getTrade().getResult().getType().equals(Material.ENCHANTED_BOOK)
                && ((EnchantmentStorageMeta)event.getTrade().getResult().getItemMeta()).hasStoredEnchant(Enchantment.MENDING)) {
            event.setCancelled(true);
        }
    }
}
