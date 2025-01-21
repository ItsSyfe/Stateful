package me.syfe.stateful.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;

import java.sql.SQLException;

import static me.syfe.stateful.util.CustomMessage.addPrefix;

public abstract class StableAbstractCommandExecutor implements CommandExecutor {
    private void checkContext (CommandSender sender, Command command, String label, String[] args) throws SQLException {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can run this command.");
            return;
        }

        if (player.isInsideVehicle() && player.getVehicle() instanceof AbstractHorse steed) {
            stableOnCommand(sender, command, label, args, player, steed);
        } else {
            player.sendMessage(
                    addPrefix(
                            Component.text("You must be riding a steed to run this command.")
                                    .color(NamedTextColor.RED)
                    )
            );
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            checkContext(sender, command, label, args);
        } catch (SQLException e) {
            sender.sendMessage("An error occurred while executing the command.");
            throw new RuntimeException(e);
        }
        return true;
    }

    protected abstract void stableOnCommand(CommandSender sender, Command command, String label, String[] args, Player player, AbstractHorse steed) throws SQLException;
}

