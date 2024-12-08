package me.syfe.stateful.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class CustomMessage {
    public static Component addPrefix(Component component) {
        return Component.text()
                .append(Component.text("[Stateful] ", TextColor.color(0x00DEE6)))
                .append(component)
                .build();
    }
}
