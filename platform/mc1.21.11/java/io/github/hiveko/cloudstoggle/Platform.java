package io.github.hiveko.cloudstoggle;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/** Minecraft 1.21.11 (obfuscated, built with Mojang mappings and remapped). */
final class Platform {
    private Platform() {}

    static KeyMapping registerKey(KeyMapping key) {
        return KeyBindingHelper.registerKeyBinding(key);
    }

    static void showOverlay(Minecraft client, Component message) {
        client.player.displayClientMessage(message, true);
    }
}
