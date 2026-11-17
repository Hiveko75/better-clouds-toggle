package io.github.hiveko.cloudstoggle;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Selects a Better Clouds shader preset made for Mellow whenever the active Iris shader pack's
 * file name starts with "Mellow" (any version or fork), and switches back when it doesn't.
 *
 * Mellow renders in linear colour and tonemaps at the end, while Better Clouds writes display
 * (gamma-encoded) colour, so under Mellow its clouds come out too bright and flat. A Better Clouds
 * preset with Gamma -2.2 linearises them first, the same as its built-in presets for other
 * linear-light packs. The preset ("Mellow (auto)") is created once as a copy of the Default
 * preset and can be tuned in Better Clouds' own settings afterwards; it is never overwritten.
 *
 * Like the toggle key, nothing is compiled against Better Clouds or Iris: both are reached by name
 * at run time, and the preset class is taken from the existing presets so it survives Better
 * Clouds moving it between packages. If anything is missing, the feature turns itself off.
 */
final class MellowPreset {
    static final String KEY = "mellow_auto";
    private static final String TITLE = "Mellow (auto)";
    private static final String PACK_PREFIX = "mellow";
    private static final float GAMMA = -2.2f;
    private static final int CHECK_INTERVAL_TICKS = 10;

    private static final Logger LOGGER = LoggerFactory.getLogger("Better Clouds Toggle");

    private static boolean disabled;
    private static int ticks;
    /** null until applied once, so the first check always sets the right preset. */
    private static Boolean lastActive;
    /** The preset that was selected before switching to the Mellow one. */
    private static Object restorePreset;

    // resolved on first use
    private static Method isInitialized, instance, handler, save;
    private static Field presets, selectedPreset;
    private static Method irisConfig, shadersEnabled, packName;

    private MellowPreset() {}

    static void tick() {
        if (disabled || ++ticks % CHECK_INTERVAL_TICKS != 0) return;
        try {
            resolve();
            if (!(boolean) isInitialized.invoke(null)) return;
            boolean active = mellowActive();
            if (lastActive != null && lastActive == active) return;
            lastActive = active;
            apply(instance.invoke(null), active);
        } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            disabled = true;
            LOGGER.warn("Automatic Mellow cloud preset turned off: Better Clouds or Iris is not compatible", e);
        }
    }

    private static boolean mellowActive() throws ReflectiveOperationException {
        if (irisConfig == null) return false;
        Object config = irisConfig.invoke(null);
        if (config == null || !(boolean) shadersEnabled.invoke(config)) return false;
        Optional<?> name = (Optional<?>) packName.invoke(config);
        return name.isPresent() && name.get().toString().toLowerCase(Locale.ROOT).startsWith(PACK_PREFIX);
    }

    @SuppressWarnings("unchecked")
    private static void apply(Object config, boolean active) throws ReflectiveOperationException {
        List<Object> list = (List<Object>) presets.get(config);
        if (list.isEmpty()) return;
        int selected = Math.clamp(selectedPreset.getInt(config), 0, list.size() - 1);
        Object current = list.get(selected);

        if (active) {
            int index = indexOfKey(list, KEY);
            if (index < 0) {
                list.add(createPreset(list));
                index = list.size() - 1;
                save.invoke(handler.invoke(null));
                LOGGER.info("Created Better Clouds preset '{}' for Mellow", TITLE);
            }
            if (!KEY.equals(key(current))) restorePreset = current;
            selectedPreset.setInt(config, index);
        } else if (KEY.equals(key(current))) {
            int index = restorePreset != null ? list.indexOf(restorePreset) : -1;
            if (index < 0) index = indexOfKey(list, "default");
            if (index < 0) {
                for (int i = 0; i < list.size(); i++) {
                    if (!KEY.equals(key(list.get(i)))) { index = i; break; }
                }
            }
            if (index >= 0) selectedPreset.setInt(config, index);
        }
    }

    /** A copy of the Default preset (or the first one) with Gamma -2.2. */
    private static Object createPreset(List<Object> list) throws ReflectiveOperationException {
        int base = indexOfKey(list, "default");
        Object source = list.get(Math.max(base, 0));
        Class<?> type = source.getClass();
        Constructor<?> copy = type.getConstructor(type);
        Object preset = copy.newInstance(source);
        type.getField("key").set(preset, KEY);
        type.getField("title").set(preset, TITLE);
        type.getField("description").set(preset,
                "Selected automatically by Better Clouds Toggle when a Mellow shader pack is active.");
        type.getField("editable").setBoolean(preset, true);
        type.getField("gamma").setFloat(preset, GAMMA);
        return preset;
    }

    private static int indexOfKey(List<Object> list, String key) throws ReflectiveOperationException {
        for (int i = 0; i < list.size(); i++) {
            if (key.equals(key(list.get(i)))) return i;
        }
        return -1;
    }

    private static Object key(Object preset) throws ReflectiveOperationException {
        return preset.getClass().getField("key").get(preset);
    }

    private static void resolve() throws ReflectiveOperationException {
        if (presets != null) return;
        ClassLoader loader = MellowPreset.class.getClassLoader();
        Class<?> manager = Class.forName("com.qendolin.betterclouds.config.ConfigManager", true, loader);
        Class<?> config = Class.forName("com.qendolin.betterclouds.config.Config", true, loader);
        Class<?> handlerType = Class.forName("dev.isxander.yacl3.config.v2.api.ConfigClassHandler", true, loader);
        isInitialized = manager.getMethod("isInitialized");
        instance = manager.getMethod("instance");
        handler = manager.getMethod("handler");
        save = handlerType.getMethod("save");
        selectedPreset = config.getField("selectedPreset");

        if (FabricLoader.getInstance().isModLoaded("iris")) {
            Class<?> iris = Class.forName("net.irisshaders.iris.Iris", true, loader);
            irisConfig = iris.getMethod("getIrisConfig");
            Class<?> irisConfigType = irisConfig.getReturnType();
            shadersEnabled = irisConfigType.getMethod("areShadersEnabled");
            packName = irisConfigType.getMethod("getShaderPackName");
        }
        presets = config.getField("presets"); // last, so a failure above retries nothing half-resolved
    }
}
