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

package me.syfe.stateful.listeners.misc;

import me.syfe.stateful.Stateful;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class BlockDamageListener implements Listener {
    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        if (Stateful.getInstance().getConfig().getBoolean("deepslateInstabreakModule")) {
            Material block = event.getBlock().getType();
            ItemStack item = event.getItemInHand();
            Player player = event.getPlayer();

            if (block == Material.DEEPSLATE
                    && item.containsEnchantment(Enchantment.EFFICIENCY)
                    && item.getEnchantmentLevel(Enchantment.EFFICIENCY) >= 5
                    && player.hasPotionEffect(PotionEffectType.HASTE)
                    && player.getPotionEffect(PotionEffectType.HASTE).getAmplifier() >= 1
                    && item.getType() == Material.NETHERITE_PICKAXE){
                event.setInstaBreak(true);
            }
        }
    }
}
