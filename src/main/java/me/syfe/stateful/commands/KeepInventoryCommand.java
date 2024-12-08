package me.syfe.stateful.commands;

import me.syfe.stateful.Stateful;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

import static me.syfe.stateful.util.CustomMessage.addPrefix;

public class KeepInventoryCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player && player.hasPermission("stateful.keepinventory")) {
            try {
                boolean newEnabledState = Stateful.getInstance().getDatabase().keepInventoryToggle(player.getUniqueId());
                player.sendMessage(
                    addPrefix(
                        Component.text("Keep Inventory is now ")
                            .append(Component.text().color(newEnabledState ? NamedTextColor.GREEN : NamedTextColor.RED).content(newEnabledState ? "enabled" : "disabled"))
                    )
                );

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }
}
