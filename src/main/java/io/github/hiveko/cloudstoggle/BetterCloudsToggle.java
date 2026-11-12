package io.github.hiveko.cloudstoggle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Toggles Better Clouds' "Enable" option with a key binding.
 *
 * Better Clouds is not compiled against or bundled: its config is reached by name at run time
 * (ConfigManager.instance().enabled, saved with ConfigManager.handler().save()), exactly the
 * field its own settings screen changes. Better Clouds checks the field every frame, so the
 * switch takes effect immediately; vanilla clouds are drawn while it is off.
 */
public class BetterCloudsToggle implements ClientModInitializer {
    public static final String MOD_ID = "better-clouds-toggle";
    private static final Logger LOGGER = LoggerFactory.getLogger("Better Clouds Toggle");

    private static KeyMapping toggleKey;

    // resolved on first use
    private static Method isInitialized, instance, handler, save;
    private static Field enabled;

    @Override
    public void onInitializeClient() {
        toggleKey = Platform.registerKey("key." + MOD_ID + ".toggle", MOD_ID, "keys");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                toggle(client);
            }
        });
    }

    private static void toggle(Minecraft client) {
        Component message;
        try {
            resolve();
            if (!(boolean) isInitialized.invoke(null)) return;
            Object config = instance.invoke(null);
            boolean now = !enabled.getBoolean(config);
            enabled.setBoolean(config, now);
            save.invoke(handler.invoke(null));
            Component state = Component.translatable(MOD_ID + ".message." + (now ? "on" : "off"))
                    .withStyle(now ? ChatFormatting.GREEN : ChatFormatting.RED);
            message = Component.translatable(MOD_ID + ".message", state).withStyle(ChatFormatting.GRAY);
        } catch (ReflectiveOperationException | RuntimeException e) {
            LOGGER.error("Could not toggle Better Clouds", e);
            message = Component.translatable(MOD_ID + ".message.failed").withStyle(ChatFormatting.RED);
        }
        if (client.player != null) {
            Platform.showOverlay(client, message);
        }
    }

    private static void resolve() throws ReflectiveOperationException {
        if (enabled != null) return;
        ClassLoader loader = BetterCloudsToggle.class.getClassLoader();
        Class<?> manager = Class.forName("com.qendolin.betterclouds.config.ConfigManager", true, loader);
        Class<?> config = Class.forName("com.qendolin.betterclouds.config.Config", true, loader);
        Class<?> handlerType = Class.forName("dev.isxander.yacl3.config.v2.api.ConfigClassHandler", true, loader);
        isInitialized = manager.getMethod("isInitialized");
        instance = manager.getMethod("instance");
        handler = manager.getMethod("handler");
        save = handlerType.getMethod("save");
        enabled = config.getField("enabled");
    }
}
