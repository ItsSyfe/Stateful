/**
 * Stateful, Minecraft Plugin for the FlowSMP
 * Copyright (C) 2024  Syfe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

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
