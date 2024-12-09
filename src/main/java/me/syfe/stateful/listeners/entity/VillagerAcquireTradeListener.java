package me.syfe.stateful.listeners.entity;

import me.syfe.stateful.Stateful;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class VillagerAcquireTradeListener implements Listener {
    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        if (Stateful.getInstance().getConfig().getBoolean("noMendingTradeModule")
                && event.getRecipe().getResult().getType().equals(Material.ENCHANTED_BOOK)
                && ((EnchantmentStorageMeta)event.getRecipe().getResult().getItemMeta()).hasStoredEnchant(Enchantment.MENDING)) {
            event.setCancelled(true);
        }
    }
}
