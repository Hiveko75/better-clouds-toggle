package io.github.hiveko.cloudstoggle;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/** Minecraft 1.21.11 (obfuscated, built with Mojang mappings and remapped). */
final class Platform {
    private Platform() {}

    /** Unbound key in the category "key.category.<namespace>.<path>". */
    static KeyMapping registerKey(String name, String namespace, String path) {
        return KeyBindingHelper.registerKeyBinding(new KeyMapping(name, InputConstants.UNKNOWN.getValue(),
                new KeyMapping.Category(Identifier.fromNamespaceAndPath(namespace, path))));
    }

    static void showOverlay(Minecraft client, Component message) {
        client.player.displayClientMessage(message, true);
    }
}
