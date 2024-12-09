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
