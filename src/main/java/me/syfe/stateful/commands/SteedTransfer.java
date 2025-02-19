package me.syfe.stateful.commands;


import me.syfe.stateful.Stateful;
import me.syfe.stateful.util.StableAbstractCommandExecutor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;

import java.sql.SQLException;

import static me.syfe.stateful.util.CustomMessage.addPrefix;

public class SteedTransfer extends StableAbstractCommandExecutor {
    public SteedTransfer() {}

    @Override
    public void stableOnCommand(CommandSender sender, Command command, String label, String[] args, Player player, AbstractHorse steed) throws SQLException {
        if (!sender.hasPermission("stateful.transfersteed")) {
            sender.sendMessage(
                    addPrefix(
                            Component.text("You do not have permission to run this command.")
                                    .color(NamedTextColor.RED)
                    )
            );
            return;
        }

        if (!(Stateful.getInstance().getDatabase().isLockedBy(steed.getUniqueId().toString(), player.getUniqueId().toString()))) {
            sender.sendMessage(
                    addPrefix(
                            Component.text("You cannot transfer someone else's steed!")
                                    .color(NamedTextColor.RED)
                    )
            );
            return;
        }

        Player transferee = Bukkit.getPlayer(args[0]);

        if (transferee == null) {
            sender.sendMessage(
                    addPrefix(
                            Component.text("You must specify a player to transfer the steed to.")
                                    .color(NamedTextColor.RED)
                    )
            );
            return;
        }

        steed.setOwner(transferee);
        Stateful.getInstance().getDatabase().transferOwnership(steed.getUniqueId().toString(), transferee.getUniqueId().toString());
        sender.sendMessage(
                addPrefix(
                        Component.text("Successfully transferred steed to " + transferee.getName() + "!")
                )
        );
        transferee.sendMessage(
                addPrefix(
                        Component.text("You have received a steed from " + player.getName() + "!")
                )
        );
    }
}

