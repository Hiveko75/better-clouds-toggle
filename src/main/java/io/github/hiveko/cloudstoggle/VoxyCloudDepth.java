package io.github.hiveko.cloudstoggle;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Lets Better Clouds hide clouds behind Voxy's distant terrain while a shader pack is active.
 *
 * Without shaders Voxy writes its terrain into Minecraft's depth buffer, so Better Clouds' normal
 * depth test already works. Shader packs with Voxy support usually keep that terrain in Voxy's own
 * depth buffer instead, which Better Clouds reads only through its Voxy compatibility class.
 * Better Clouds 1.15.x ships that class (VoxyCompatImpl) but switches it off with a constant
 * (ModLoaded.VOXY = false), so clouds were drawn in front of distant mountains.
 *
 * This installs VoxyCompatImpl as Better Clouds' Voxy compat once Better Clouds has set up its own
 * (disabled) one. Better Clouds then picks up Voxy's depth texture on its next frame. Nothing is
 * compiled against Better Clouds or Voxy; if a class or method is missing, it leaves things as they
 * were.
 */
final class VoxyCloudDepth {
    private static final Logger LOGGER = LoggerFactory.getLogger("Better Clouds Toggle");
    private static final String COMPAT = "com.qendolin.betterclouds.compat.VoxyCompat";
    private static final String COMPAT_IMPL = "com.qendolin.betterclouds.compat.VoxyCompatImpl";

    private static boolean done;

    private VoxyCloudDepth() {}

    static void tick() {
        if (done) return;
        if (!FabricLoader.getInstance().isModLoaded("voxy")) {
            done = true;
            return;
        }
        try {
            ClassLoader loader = VoxyCloudDepth.class.getClassLoader();
            Field instance = Class.forName(COMPAT, true, loader).getField("instance");
            Object current = instance.get(null);
            if (current == null) return; // Better Clouds hasn't initialised its compat yet
            done = true;

            Class<?> impl = Class.forName(COMPAT_IMPL, true, loader);
            if (impl.isInstance(current)) return; // Better Clouds already enabled it itself

            Object compat = impl.getConstructor().newInstance();
            // Touch the Voxy-facing methods now, so a Voxy/Better Clouds mismatch fails here
            // (and is skipped) instead of while rendering clouds.
            impl.getMethod("isEnabled").invoke(compat);
            impl.getMethod("isZNeg1To1").invoke(compat);
            impl.getMethod("putProjMat", float[].class).invoke(compat, (Object) new float[16]);

            instance.set(null, compat);
            LOGGER.info("Enabled Better Clouds' Voxy support, so clouds are hidden behind Voxy terrain with shaders");
        } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            done = true;
            LOGGER.warn("Could not enable Better Clouds' Voxy support; clouds may show in front of Voxy terrain with shaders", e);
        }
    }
}
