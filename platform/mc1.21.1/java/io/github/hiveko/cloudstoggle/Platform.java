package io.github.hiveko.cloudstoggle;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/** Minecraft 1.21 - 1.21.1 (obfuscated, built with Mojang mappings and remapped). */
final class Platform {
    private Platform() {}

    /** Unbound key; before 1.21.9 the category is its translation key, "key.category.<namespace>.<path>". */
    static KeyMapping registerKey(String name, String namespace, String path) {
        return KeyBindingHelper.registerKeyBinding(new KeyMapping(name, InputConstants.UNKNOWN.getValue(),
                "key.category." + namespace + "." + path));
    }

    static void showOverlay(Minecraft client, Component message) {
        client.player.displayClientMessage(message, true);
    }
}
