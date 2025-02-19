package me.syfe.stateful.commands;

import me.syfe.stateful.Stateful;
import me.syfe.stateful.util.StableAbstractCommandExecutor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;

import java.sql.SQLException;

import static me.syfe.stateful.util.CustomMessage.addPrefix;

public class LockSteed extends StableAbstractCommandExecutor {
    public LockSteed() {}

    @Override
    public void stableOnCommand(CommandSender sender, Command command, String label, String[] args, Player player, AbstractHorse steed) throws SQLException {
        if (!sender.hasPermission("stateful.locksteed")) {
            player.sendMessage(
                    addPrefix(
                            Component.text("You do not have permission to run this command.")
                                    .color(NamedTextColor.RED)
                    )
            );
            return;
        }

        if (Stateful.getInstance().getDatabase().isLocked(steed.getUniqueId().toString())
                && !(Stateful.getInstance().getDatabase().isLockedBy(steed.getUniqueId().toString(), player.getUniqueId().toString()))) {
            player.sendMessage(
                    addPrefix(
                            Component.text("You cannot lock someone else's steed!")
                                    .color(NamedTextColor.RED)
                    )
            );
            return;
        }

        boolean lockStatus = Stateful.getInstance().getDatabase().toggleSteedLock(steed.getUniqueId().toString(), player.getUniqueId().toString());
        if (lockStatus) {
            player.sendMessage(
                    addPrefix(
                            Component.text("Steed locked.")
                    )
            );
        } else {
            player.sendMessage(
                    addPrefix(
                            Component.text("Steed unlocked.")
                    )
            );
        }
    }
}