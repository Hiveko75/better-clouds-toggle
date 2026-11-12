package io.github.hiveko.cloudstoggle;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/** Minecraft 26.1.2 - 26.3 (unobfuscated). */
final class Platform {
    private Platform() {}

    /** Unbound key in the category "key.category.<namespace>.<path>". */
    static KeyMapping registerKey(String name, String namespace, String path) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(name, InputConstants.UNKNOWN.getValue(),
                new KeyMapping.Category(Identifier.fromNamespaceAndPath(namespace, path))));
    }

    static void showOverlay(Minecraft client, Component message) {
        client.player.sendOverlayMessage(message);
    }
}
