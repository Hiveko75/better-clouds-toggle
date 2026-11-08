package io.github.hiveko.cloudstoggle;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/** Minecraft 26.1.2 - 26.3 (unobfuscated). */
final class Platform {
    private Platform() {}

    static KeyMapping registerKey(KeyMapping key) {
        return KeyMappingHelper.registerKeyMapping(key);
    }

    static void showOverlay(Minecraft client, Component message) {
        client.player.sendOverlayMessage(message);
    }
}
