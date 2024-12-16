package me.syfe.stateful.commands;

import me.syfe.stateful.Stateful;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import static me.syfe.stateful.util.CustomMessage.addPrefix;

public class StaffModeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("stateful.staffmode")) {
            sender.sendMessage(
                    addPrefix(
                            Component.text("You do not have permission to use that command", NamedTextColor.RED)
                    )
            );
            return false;
        }
        if (sender instanceof Player) {
            Player player = (Player)sender;
            try {
                boolean mode = Stateful.getInstance().getDatabase().toggleStaffMode(player.getUniqueId().toString());
                if (mode) {
                    Stateful.getInstance().getDatabase().savePlayerState(player.getUniqueId().toString(), player.getInventory().getContents(), Float.valueOf(player.getExp()), Integer.valueOf(player.getLevel()), player.getLocation());
                    player.getInventory().clear();
                    player.setExp(0.0F);
                    player.setLevel(0);
                    player.setGameMode(GameMode.CREATIVE);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, -1, 1, false, false));
                    player.sendMessage(
                            addPrefix(
                                    Component.text("Staff Mode ")
                                            .append(Component.text()
                                                    .color(NamedTextColor.GREEN)
                                                    .content("enabled")
                                            )
                            )
                    );
                } else {
                    Stateful.getInstance().getDatabase().restorePlayerState(player.getUniqueId().toString(), player);
                    player.setGameMode(GameMode.SURVIVAL);
                    player.removePotionEffect(PotionEffectType.INVISIBILITY);
                    player.sendMessage(
                            addPrefix(
                                    Component.text("Staff Mode ")
                                            .append(Component.text()
                                                    .color(NamedTextColor.RED)
                                                    .content("disabled")
                                            )
                            )
                    );
                }
                return true;
            } catch (Exception e) {
                player.sendMessage(
                        addPrefix(
                                Component.text("An error occured while executing that command", NamedTextColor.RED)
                        )
                );
                e.printStackTrace();
                return false;
            }
        }

        sender.sendMessage(
                addPrefix(
                        Component.text("Cannot execute command from console", NamedTextColor.RED)
                )
        );
        return true;
    }
}
