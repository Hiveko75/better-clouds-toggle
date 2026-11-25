# Better Clouds Toggle

> **No longer developed (25 Nov 2026).** Its features are now part of
> [Vanilla Finest Fixes](https://github.com/Hiveko75/vanilla-finest-fixes), the fixes mod of the
> [Vanilla Finest](https://modrinth.com/modpack/vanilla-finest-survival) modpacks (same key binding). This repository is archived: the code and the released jars stay available,
> but there will be no new versions.

A small client-side Fabric add-on for [Better Clouds](https://github.com/Qendolin/better-clouds)
that adds a key binding to switch Better Clouds on and off, the same as its **Enable** option.

- Bind it under *Options → Controls → Key Binds → Better Clouds → Toggle Better Clouds* (unbound by default).
- Takes effect immediately and is saved to Better Clouds' own config. While off, vanilla clouds are shown.
- Better Clouds is not modified, bundled or compiled against; this mod only flips its existing setting.

## Mellow shader preset

Mellow works in linear colour, so Better Clouds' clouds look too bright and flat under it. When the
active Iris shader pack's file name starts with **Mellow** (any version or fork), this mod selects a
Better Clouds shader preset called **Mellow (auto)**, and switches back to the previous preset when
you change pack or turn shaders off.

- The preset is created once as a copy of Better Clouds' *Default* preset with **Gamma -2.2**,
  **Day Brightness 0.5**, **Saturation 2.0** and **Opacity Factor 0.5**.
  Tune it under *Better Clouds → Shaders*; your changes are kept.
- Better Clouds still needs **Iris Support** on to draw with shaders at all.

## Clouds behind Voxy terrain with shaders

Better Clouds 1.15 ships its Voxy support switched off, so with a shader pack its clouds were drawn
in front of distant Voxy mountains. When Voxy is installed, this mod turns that built-in support on
(nothing is added to Better Clouds), so clouds are hidden behind Voxy terrain again.

Requires Better Clouds and Fabric API. Iris and Voxy are optional.

| Jar | Minecraft |
|---|---|
| `better-clouds-toggle-<version>+mc26.1.2-26.3.jar` | 26.1.2, 26.2, 26.3 |
| `better-clouds-toggle-<version>+mc1.21.11.jar` | 1.21.11 |
| `better-clouds-toggle-<version>+mc1.21.1.jar` | 1.21, 1.21.1 |

Building: `./gradlew build` (26.x jar) `./gradlew -p mc1.21.11 build` (1.21.11 jar) and `./gradlew -p mc1.21.1 build` (1.21.1 jar).
CI also compiles the 26.x sources against each 26.x version and checks the bytecode is identical.

License: MIT. Better Clouds is MPL-2.0 by Qendolin.
