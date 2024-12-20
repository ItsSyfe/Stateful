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
