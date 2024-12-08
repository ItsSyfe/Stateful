package me.syfe.stateful.util;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class CustomMessage {
    public static void createMessage(Player player, String message) {
        Component component = Component.(Component.text(), Component.text(message));
        player.sendMessage(component);
    }
}
