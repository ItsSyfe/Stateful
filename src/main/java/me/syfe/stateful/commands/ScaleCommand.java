package me.syfe.stateful.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.syfe.stateful.util.CustomMessage.addPrefix;

public class ScaleCommand implements CommandExecutor {
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

        double scale;
        try {
            scale = Double.parseDouble(args[0].equalsIgnoreCase("reset") ? "1" : args[0]);
            if (scale < 0.25f || scale > 1.5f) throw new NumberFormatException();
        } catch (Exception _e) {
            sender.sendMessage(
                    addPrefix(
                            Component.text("Invalid scale value", NamedTextColor.RED)
                    )
            );
            return true;
        }

        if (sender instanceof Player player) {
            player.getAttribute(Attribute.SCALE).setBaseValue(scale);
            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(Math.min(20*scale, 20));

            player.sendMessage(
                    addPrefix(
                            Component.text("Scale set to " + scale, NamedTextColor.GREEN)
                    )
            );
            return true;
        }

        sender.sendMessage(
                addPrefix(
                        Component.text("Cannot execute command from console", NamedTextColor.RED)
                )
        );
        return true;
    }
}
